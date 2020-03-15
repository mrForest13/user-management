package com.mforest.example.application

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.mforest.example.application.initialization.{ApiInitializer, DaoInitializer, ServiceInitializer}
import com.mforest.example.core.ConfigLoader
import com.mforest.example.db.Database
import com.mforest.example.db.cache.Cache
import com.mforest.example.db.migration.MigrationManager
import com.mforest.example.http.Server
import org.http4s.server.{Server => BlazeServer}

object Application extends IOApp {

  private def initialize[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, BlazeServer[F]] = {
    for {
      config     <- ConfigLoader[F].config()
      database   = config.database
      pool       <- Cache[F](database.redis).pool()
      transactor <- Database[F](database.postgres).transactor()
      _          = MigrationManager[F](database).migrate(transactor)
      dao        = DaoInitializer()
      service    = ServiceInitializer(config.auth, dao, pool, transactor)
      api        = ApiInitializer(config, service)
      server     <- Server[F](config, api.apis).resource
    } yield server
  }

  override def run(args: List[String]): IO[ExitCode] = {
    initialize.use(_ => IO.never).as(ExitCode.Success)
  }
}
