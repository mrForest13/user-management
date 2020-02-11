package com.mforest.example.service.auth

import cats.effect.Async
import com.mforest.example.db.dao.UserDao
import com.mforest.example.db.row.UserRow
import com.mforest.example.service.Service
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import tsec.authentication.{BackingStore, BearerTokenAuthenticator, IdentityStore, TSecBearerToken, TSecTokenSettings}
import tsec.common.SecureRandomId

import scala.concurrent.duration._

trait AuthService extends Service

class AuthServiceImpl[F[_]: Async](userDao: UserDao, transactor: Transactor[F]) extends AuthService {

  val settings: TSecTokenSettings = TSecTokenSettings(
    expiryDuration = 10.minutes,
    maxIdle = None
  )

  val tokenStore: BackingStore[F, SecureRandomId, TSecBearerToken[FUUID]] = TokenStore[F]
  val userStore: IdentityStore[F, FUUID, UserRow]                         = UserStore[F](userDao, transactor)

  val bearerTokenAuth: BearerTokenAuthenticator[F, FUUID, UserRow] =
    BearerTokenAuthenticator(tokenStore, userStore, settings)

  def createToken(id: FUUID): F[TSecBearerToken[FUUID]] = {
    bearerTokenAuth.create(id)
  }
}
