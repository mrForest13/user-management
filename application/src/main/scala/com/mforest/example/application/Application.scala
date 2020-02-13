package com.mforest.example.application

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.mforest.example.application.info.BuildInfo
import com.mforest.example.application.layer.DaoLayer
import com.mforest.example.core.ConfigLoader
import com.mforest.example.db.Database
import com.mforest.example.http.Server
import com.mforest.example.http.api.{LoginApi, PermissionApi, RegistrationApi, SwaggerApi}
import com.mforest.example.http.yaml.OpenApi
import com.mforest.example.service.hash.SCryptEngine
import com.mforest.example.service.login.LoginService
import com.mforest.example.service.permission.PermissionService
import com.mforest.example.service.registration.RegistrationService
import doobie.util.ExecutionContexts
import org.http4s.server.{Server => BlazeServer}
import tsec.passwordhashers.jca.SCrypt

object Application extends IOApp with DaoLayer {

  private def initApplication[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, BlazeServer[F]] = {
    for {
      config              <- ConfigLoader[F].load
      connEc              <- ExecutionContexts.fixedThreadPool[F](config.database.poolSize)
      txnEc               <- ExecutionContexts.cachedThreadPool[F]
      transactor          <- Database[F](config.database).transactor(connEc, txnEc)
      hashEngine          = SCryptEngine[F]()
      registrationService = RegistrationService[F, SCrypt](userDao, hashEngine, transactor)
      permissionService   = PermissionService[F](permissionDao, transactor)
      loginService        = LoginService[F, SCrypt](userDao, hashEngine, transactor)
      registrationApi     = RegistrationApi[F](registrationService)
      permissionApi       = PermissionApi[F](permissionService)
      loginApi            = LoginApi[F](loginService)
      docs                = OpenApi(config.app, BuildInfo.version)
      swaggerApi          = SwaggerApi(docs.yaml)
      server              <- Server[F](config, registrationApi, permissionApi, loginApi, swaggerApi).resource
    } yield server
  }

  override def run(args: List[String]): IO[ExitCode] = {
    initApplication.use(_ => IO.never).as(ExitCode.Success)
  }
}
