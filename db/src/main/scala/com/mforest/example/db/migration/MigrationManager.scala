package com.mforest.example.db.migration

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Async, Sync}
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

class MigrationManager[F[_]: Async](config: DatabaseConfig) {

  def migrate(transactor: HikariTransactor[F]): F[Unit] = migrate {
    flyway(transactor)
  }

  def migrate(): F[Unit] = migrate {
    flyway()
  }

  private def flyway(transactor: HikariTransactor[F]): Flyway = {
    Flyway
      .configure()
      .dataSource(transactor.kernel)
      .table(config.migrationTable)
      .load()
  }

  private def flyway(): Flyway = {
    Flyway
      .configure()
      .dataSource(config.postgresUrl, config.user, config.password)
      .table(config.migrationTable)
      .load()
  }

  private def migrate(flyway: Flyway)(implicit F: Sync[F]): F[Unit] = {
    if (config.migrate) F.delay(flyway.migrate()).as(()) else F.pure(())
  }
}

object MigrationManager {

  def apply[F[_]: Async](config: DatabaseConfig): MigrationManager[F] = new MigrationManager(config)
}
