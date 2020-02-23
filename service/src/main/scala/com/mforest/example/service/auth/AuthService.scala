package com.mforest.example.service.auth

import cats.Id
import cats.data.{EitherT, NonEmptyChain, OptionT}
import cats.effect.Sync
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.ForbiddenError
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.Service
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.store.{BarerTokenStore, PermissionsStore}
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import org.http4s.Request
import tsec.authentication.{BearerTokenAuthenticator, SecuredRequest, TSecBearerToken, TSecTokenSettings}

trait AuthService[F[_]] extends Service {

  val name: String = "Auth-Service"

  def authorize(raw: String, permission: String): EitherT[F, Error, AuthInfo]
  def validateToken(raw: String): EitherT[F, Error, AuthInfo]
  def createToken(identity: Id[FUUID]): F[TSecBearerToken[Id[FUUID]]]
  def discardToken(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]]

  case class AuthInfo(identity: NonEmptyChain[PermissionDto], authenticator: TSecBearerToken[Id[FUUID]])
}

class AuthServiceImpl[F[_]: Sync](val auth: BearerTokenAuthenticator[F, Id[FUUID], NonEmptyChain[PermissionDto]])
    extends AuthService[F] {

  private val forbidden: String = "The server is refusing to respond to it! You don't have permission!"

  override def validateToken(raw: String): EitherT[F, Error, AuthInfo] = {
    auth
      .parseRaw(raw, Request())
      .map(info)
      .toRight(ForbiddenError(forbidden))
  }

  override def createToken(identity: Id[FUUID]): F[TSecBearerToken[Id[FUUID]]] = {
    auth.create(identity)
  }

  override def discardToken(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    auth.discard(token)
  }

  override def authorize(raw: String, permission: String): EitherT[F, Error, AuthInfo] = {
    validateToken(raw).flatMap { info =>
      OptionT
        .fromOption(info.identity.find(_.name == permission))
        .map(_ => info)
        .toRight(ForbiddenError(forbidden))
    }
  }

  private def info(request: SecuredRequest[F, NonEmptyChain[PermissionDto], TSecBearerToken[Id[FUUID]]]): AuthInfo = {
    AuthInfo(request.identity, request.authenticator)
  }
}

object AuthService {

  def apply[F[_]: Sync](dao: PermissionDao, transactor: Transactor[F], config: TokenConfig): AuthService[F] = {

    val tokenStore    = BarerTokenStore[F]
    val identityStore = PermissionsStore[F](dao, transactor)
    val settings      = TSecTokenSettings(config.expiryDuration, config.maxIdle)

    new AuthServiceImpl(BearerTokenAuthenticator.apply(tokenStore, identityStore, settings))
  }
}
