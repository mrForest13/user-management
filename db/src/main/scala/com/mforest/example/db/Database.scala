package com.mforest.example.db

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Async, ContextShift, Resource, Sync}
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

case class Database[F[_]: Async: ContextShift](config: DatabaseConfig) {

  def transactor(connEc: ExecutionContext, txnEc: ExecutionContext): Resource[F, HikariTransactor[F]] = {
    for {
      transactor <- initTransactor(connEc, txnEc)
      flyway     <- flyway(transactor)
      _          <- migrate(flyway)
    } yield transactor
  }

  private def initTransactor(connEc: ExecutionContext, txnEc: ExecutionContext): Resource[F, HikariTransactor[F]] = {
    HikariTransactor.newHikariTransactor[F](
      driverClassName = config.driver,
      url = config.postgresUrl,
      user = config.user,
      pass = config.password,
      connectEC = connEc,
      transactEC = txnEc
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
