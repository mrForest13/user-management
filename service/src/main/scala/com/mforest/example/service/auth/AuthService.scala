package com.mforest.example.service.auth

import cats.data.{EitherT, NonEmptyChain, OptionT}
import cats.effect.Async
import cats.{Id, Show}
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.core.error.Error
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.Service
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.SessionInfo
import com.mforest.example.service.store.{BearerTokenStore, PermissionsStore}
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import org.http4s.Request
import redis.clients.jedis.JedisPool
import tsec.authentication.{BearerTokenAuthenticator, TSecBearerToken, TSecTokenSettings}

trait AuthService[F[_]] extends Service {

  val name: String = "Auth-Service"

  def authorize[P: Show](raw: String, permission: P): EitherT[F, Error, SessionInfo]
  def validateAndRenew(raw: String): EitherT[F, Error, SessionInfo]
  def create(identity: Id[FUUID]): F[TSecBearerToken[Id[FUUID]]]
  def discard(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]]
}

class AuthServiceImpl[F[_]: Async](auth: BearerTokenAuthenticator[F, Id[FUUID], NonEmptyChain[PermissionDto]])
    extends AuthService[F] {

  private val forbidden: String = "The server is refusing to respond to it! You don't have permission!"

  override def validateAndRenew(raw: String): EitherT[F, Error, SessionInfo] = {
    for {
      info      <- validate(raw)
      refreshed <- renew(info.authenticator)
    } yield SessionInfo(info.identity, refreshed)
  }

  override def create(identity: Id[FUUID]): F[TSecBearerToken[Id[FUUID]]] = {
    auth.create(identity)
  }

  override def discard(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    auth.discard(token)
  }

  override def authorize[P: Show](raw: String, permission: P): EitherT[F, Error, SessionInfo] = {
    validateAndRenew(raw).flatMap { info =>
      OptionT
        .fromOption(info.identity.find(_.name == permission.show))
        .toRight[Error](Error.ForbiddenError(forbidden))
        .as(info)
    }
  }

  private def renew(token: TSecBearerToken[Id[FUUID]]): EitherT[F, Error, TSecBearerToken[Id[FUUID]]] = {
    EitherT.right(auth.renew(token))
  }

  private def validate(raw: String): EitherT[F, Error, SessionInfo] = {
    auth
      .parseRaw(raw, Request())
      .toRight[Error](Error.ForbiddenError(forbidden))
      .map(SessionInfo.apply[F])
  }
}

object AuthService {

  def apply[F[_]: Async](
      dao: PermissionDao,
      transactor: Transactor[F],
      client: JedisPool,
      config: TokenConfig
  ): AuthService[F] = {

    val tokenStore    = BearerTokenStore[F](client, config)
    val identityStore = PermissionsStore[F](dao, client, transactor)
    val settings      = TSecTokenSettings(config.expiryDuration, config.maxIdle)

    new AuthServiceImpl(BearerTokenAuthenticator.apply(tokenStore, identityStore, settings))
  }
}
