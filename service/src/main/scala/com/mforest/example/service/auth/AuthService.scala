package com.mforest.example.service.auth

import cats.Id
import cats.data.{EitherT, NonEmptyChain, OptionT}
import cats.effect.Async
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.core.error.Error
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.Service
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.AuthInfo
import com.mforest.example.service.store.{BarerTokenStore, PermissionsStore}
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import org.http4s.Request
import redis.clients.jedis.JedisPool
import tsec.authentication.{BearerTokenAuthenticator, SecuredRequest, TSecBearerToken, TSecTokenSettings}

trait AuthService[F[_]] extends Service {

  val name: String = "Auth-Service"

  def authorize(raw: String, permission: String): EitherT[F, Error, AuthInfo]
  def validateAndRenew(raw: String): EitherT[F, Error, AuthInfo]
  def create(identity: Id[FUUID]): F[TSecBearerToken[Id[FUUID]]]
  def discard(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]]
}

class AuthServiceImpl[F[_]: Async](auth: BearerTokenAuthenticator[F, Id[FUUID], NonEmptyChain[PermissionDto]])
    extends AuthService[F] {

  private val forbidden: String = "The server is refusing to respond to it! You don't have permission!"

  override def validateAndRenew(raw: String): EitherT[F, Error, AuthInfo] = {
    for {
      info      <- validate(raw)
      refreshed <- renew(info.authenticator)
    } yield AuthInfo(info.identity, refreshed)
  }

  override def create(identity: Id[FUUID]): F[TSecBearerToken[Id[FUUID]]] = {
    auth.create(identity)
  }

  override def discard(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    auth.discard(token)
  }

  override def authorize(raw: String, permission: String): EitherT[F, Error, AuthInfo] = {
    validateAndRenew(raw).flatMap { info =>
      OptionT
        .fromOption(info.identity.find(_.name == permission))
        .toRight(Error.forbidden(forbidden))
        .as(info)
    }
  }

  private def renew(token: TSecBearerToken[Id[FUUID]]): EitherT[F, Error, TSecBearerToken[Id[FUUID]]] = {
    EitherT.right[Error](auth.renew(token))
  }

  private def validate(raw: String): EitherT[F, Error, AuthInfo] = {
    auth
      .parseRaw(raw, Request())
      .toRight(Error.forbidden(forbidden))
      .map(info)
  }

  private def info(request: SecuredRequest[F, NonEmptyChain[PermissionDto], TSecBearerToken[Id[FUUID]]]): AuthInfo = {
    AuthInfo(request.identity, request.authenticator)
  }
}

object AuthService {

  def apply[F[_]: Async](
      dao: PermissionDao,
      transactor: Transactor[F],
      client: JedisPool,
      config: TokenConfig
  ): AuthService[F] = {

    val tokenStore    = BarerTokenStore[F](client, config)
    val identityStore = PermissionsStore[F](dao, client, transactor)
    val settings      = TSecTokenSettings(config.expiryDuration, config.maxIdle)

    new AuthServiceImpl(BearerTokenAuthenticator.apply(tokenStore, identityStore, settings))
  }
}
