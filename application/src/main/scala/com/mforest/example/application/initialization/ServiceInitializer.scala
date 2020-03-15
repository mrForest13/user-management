package com.mforest.example.application.initialization

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import com.mforest.example.core.config.Config
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.hash.{HashEngine, SCryptEngine}
import com.mforest.example.service.health.HealthCheckService
import com.mforest.example.service.login.LoginService
import com.mforest.example.service.permission.PermissionService
import com.mforest.example.service.registration.RegistrationService
import com.mforest.example.service.user.UserService
import doobie.util.transactor.Transactor
import redis.clients.jedis.JedisPool
import tsec.passwordhashers.jca.SCrypt

class ServiceInitializer[F[_]: ContextShift: ConcurrentEffect: Timer](
    config: Config,
    dao: DaoInitializer,
    pool: JedisPool,
    transactor: Transactor[F]
) {

  private val engine: HashEngine[F, SCrypt] = SCryptEngine[F]()

  val registration: RegistrationService[F] = RegistrationService[F, SCrypt](dao.user, engine, transactor)
  val healthCheck: HealthCheckService[F]   = HealthCheckService[F](transactor, pool, config.healthCheck)
  val permission: PermissionService[F]     = PermissionService[F](dao.permission, transactor)
  val login: LoginService[F]               = LoginService[F, SCrypt](dao.user, engine, transactor)
  val user: UserService[F]                 = UserService[F](dao.user, dao.permission, pool, transactor)
  val auth: AuthService[F]                 = AuthService[F](dao.permission, transactor, pool, config.auth.token)
}

object ServiceInitializer {

  def apply[F[_]: ContextShift: ConcurrentEffect: Timer](
      config: Config,
      dao: DaoInitializer,
      pool: JedisPool,
      transactor: Transactor[F]
  ): ServiceInitializer[F] = new ServiceInitializer(config, dao, pool, transactor)
}
