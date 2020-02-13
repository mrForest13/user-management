package com.mforest.example.service.auth

import cats.effect.Sync
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.db.dao.UserDao
import com.mforest.example.service.Service
import com.mforest.example.service.store.{BarerTokenStore, UserStore}
import doobie.util.transactor.Transactor
import tsec.authentication.{Authenticator, BearerTokenAuthenticator, TSecBearerToken, TSecTokenSettings}

trait AuthService[F[_], I, V, A] extends Service {

  def create(identity: I): F[A]
}

class AuthServiceImpl[F[_]: Sync, I, V, A](val authenticator: Authenticator[F, I, V, A])
    extends AuthService[F, I, V, A] {

  override def create(identity: I): F[A] = {
    authenticator.create(identity)
  }
}

object AuthService {

  def apply[F[_]: Sync, I, V](
      userDao: UserDao,
      transactor: Transactor[F],
      config: TokenConfig
  ): AuthService[F, I, V, TSecBearerToken[I]] = {

    val tokenStore    = BarerTokenStore[F]
    val identityStore = UserStore[F](userDao, transactor)
    val settings      = TSecTokenSettings(config.expiryDuration, config.maxIdle)

    new AuthServiceImpl(BearerTokenAuthenticator.apply(tokenStore, identityStore, settings))
  }
}
