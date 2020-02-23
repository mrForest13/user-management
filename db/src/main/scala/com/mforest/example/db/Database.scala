package com.mforest.example.db

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

class Database[F[_]: Async: ContextShift](config: DatabaseConfig) {

  def transactor(connectEC: ExecutionContext, blocker: Blocker): Resource[F, HikariTransactor[F]] = {
    for {
      transactor <- initTransactor(connectEC, blocker)
      flyway     <- flyway(transactor)
      _          <- migrate(flyway)
    } yield transactor
  }

  private def initTransactor(connectEC: ExecutionContext, blocker: Blocker): Resource[F, HikariTransactor[F]] = {
    HikariTransactor.newHikariTransactor[F](
      driverClassName = config.driver,
      url = config.postgresUrl,
      user = config.user,
      pass = config.password,
      connectEC = connectEC,
      blocker = blocker
    )
  }

  private def flyway(transactor: HikariTransactor[F]): Resource[F, Flyway] = Resource.pure {
    Flyway
      .configure()
      .dataSource(transactor.kernel)
      .table(config.migrationTable)
      .load()
  }

  private def migrate(flyway: Flyway)(implicit F: Sync[F]): Resource[F, Unit] = Resource.liftF {
    if (config.migrate) F.delay(flyway.migrate()).as(()) else F.pure(())
  }
}

object Database {

  def apply[F[_]: Async: ContextShift](config: DatabaseConfig): Database[F] = new Database(config)
}
