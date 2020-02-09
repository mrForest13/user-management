package com.mforest.example.application

import cats.Functor.ops.toAllFunctorOps
import cats.SemigroupK.nonInheritedOps._
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.mforest.example.application.info.BuildInfo
import com.mforest.example.core.ConfigLoader
import com.mforest.example.db.Database
import com.mforest.example.db.dao.UserDao
import com.mforest.example.http.Server
import com.mforest.example.http.api.RegistrationApi
import com.mforest.example.http.swagger.OpenApi
import com.mforest.example.service.hash.SCryptEngine
import com.mforest.example.service.registration.RegistrationService
import doobie.util.ExecutionContexts
import org.http4s.server.{Server => BlazeServer}
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import tsec.passwordhashers.jca.SCrypt

object Application extends IOApp {

  private def initApplication[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, BlazeServer[F]] = {
    for {
      config     <- ConfigLoader[F].load
      connEc     <- ExecutionContexts.fixedThreadPool[F](config.database.poolSize)
      txnEc      <- ExecutionContexts.cachedThreadPool[F]
      transactor <- Database[F](config.database).transactor(connEc, txnEc)
      userDao    = UserDao()
      hashEngine = SCryptEngine[F]()
      regService = RegistrationService[F, SCrypt](userDao, hashEngine, transactor)
      userApi    = RegistrationApi[F](regService)
      docs       = OpenApi(config.app, BuildInfo.version)
      swaggerApi = new SwaggerHttp4s(docs.yaml)
      server     <- Server[F](config, userApi.routes <+> swaggerApi.routes).resource
    } yield server
  }

  override def run(args: List[String]): IO[ExitCode] = {
    initApplication.use(_ => IO.never).as(ExitCode.Success)
  }
}
