package com.mforest.example.service.auth

import cats.Id
import cats.data.{EitherT, NonEmptyChain}
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
import tsec.authentication.{Authenticator, BearerTokenAuthenticator, SecuredRequest, TSecBearerToken, TSecTokenSettings}

trait AuthService[F[_], I, V, A] extends Service {

  def validateToken(raw: String): EitherT[F, Error, AuthInfo]
  def createToken(identity: I): F[A]
  def discardToken(token: A): F[A]

  case class AuthInfo(identity: V, authenticator: A)
}

class AuthServiceImpl[F[_]: Sync, I, V, A](val auth: Authenticator[F, I, V, A]) extends AuthService[F, I, V, A] {

  private val forbidden: String = "The server is refusing to respond to it! You don't have permission!"

  override def validateToken(raw: String): EitherT[F, Error, AuthInfo] = {
    auth
      .parseRaw(raw, Request())
      .map(info)
      .toRight(ForbiddenError(forbidden))
  }

  override def createToken(identity: I): F[A] = {
    auth.create(identity)
  }

  override def discardToken(token: A): F[A] = {
    auth.discard(token)
  }

  private def info(request: SecuredRequest[F, V, A]): AuthInfo = {
    AuthInfo(request.identity, request.authenticator)
  }
}

object AuthService {

  def apply[F[_]: Sync](
      permissionDao: PermissionDao,
      transactor: Transactor[F],
      config: TokenConfig
  ): AuthService[F, Id[FUUID], NonEmptyChain[PermissionDto], TSecBearerToken[Id[FUUID]]] = {

    val tokenStore    = BarerTokenStore[F]
    val identityStore = PermissionsStore[F](permissionDao, transactor)
    val settings      = TSecTokenSettings(config.expiryDuration, config.maxIdle)

    new AuthServiceImpl(BearerTokenAuthenticator.apply(tokenStore, identityStore, settings))
  }
}
