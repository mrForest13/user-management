package com.mforest.example.application

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.mforest.example.application.info.BuildInfo
import com.mforest.example.core.ConfigLoader
import com.mforest.example.db.Database
import com.mforest.example.db.cache.Cache
import com.mforest.example.db.dao.{PermissionDao, UserDao}
import com.mforest.example.db.migration.MigrationManager
import com.mforest.example.http.Server
import com.mforest.example.http.api.{AuthenticationApi, AuthorizationApi, PermissionApi, RegistrationApi, SwaggerApi, UserApi}
import com.mforest.example.http.yaml.SwaggerDocs
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.hash.SCryptEngine
import com.mforest.example.service.login.LoginService
import com.mforest.example.service.permission.PermissionService
import com.mforest.example.service.registration.RegistrationService
import com.mforest.example.service.user.UserService
import doobie.util.ExecutionContexts
import org.http4s.server.{Server => BlazeServer}
import tsec.passwordhashers.jca.SCrypt

object Application extends IOApp {

  private def initApplication[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, BlazeServer[F]] = {
    for {
      config              <- ConfigLoader[F].load
      connectEC           <- ExecutionContexts.fixedThreadPool[F](config.database.postgres.connectPoolSize)
      blocker             <- Blocker[F]
      transactor          <- Database[F](config.database.postgres).transactor(connectEC, blocker)
      _                   = MigrationManager[F](config.database).migrate(transactor)
      pool                <- Cache[F](config.database.redis).pool()
      userDao             = UserDao()
      permissionDao       = PermissionDao()
      hashEngine          = SCryptEngine[F]()
      registrationService = RegistrationService[F, SCrypt](userDao, hashEngine, transactor)
      permissionService   = PermissionService[F](permissionDao, transactor)
      loginService        = LoginService[F, SCrypt](userDao, hashEngine, transactor)
      userService         = UserService[F](userDao, permissionDao, pool, transactor)
      authService         = AuthService[F](permissionDao, transactor, pool, config.auth.token)
      registrationApi     = RegistrationApi[F](registrationService)
      permissionApi       = PermissionApi[F](permissionService, authService)
      authenticationApi   = AuthenticationApi(loginService, authService)
      authorizationApi    = AuthorizationApi(authService)
      userApi             = UserApi[F](userService, authService)
      apisWithDocs        = Seq(registrationApi, permissionApi, authenticationApi, authorizationApi, userApi)
      swaggerDocs         = SwaggerDocs(config.app, BuildInfo.version, apisWithDocs)
      swaggerApi          = SwaggerApi[F](swaggerDocs.yaml, config.swagger)
      server              <- Server[F](config, apisWithDocs.+:(swaggerApi)).resource
    } yield server
  }

  override def run(args: List[String]): IO[ExitCode] = {
    initApplication.use(_ => IO.never).as(ExitCode.Success)
  }
}
