package com.mforest.example.db.migration

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Async, Sync}
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

final class MigrationManager[F[_]: Async](config: DatabaseConfig) {

  private val url: String      = config.postgres.postgresUrl
  private val user: String     = config.postgres.user
  private val table: String    = config.migration.migrationTable
  private val password: String = config.postgres.password

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
      .table(table)
      .load()
  }

  private def flyway(): Flyway = {
    Flyway
      .configure()
      .dataSource(url, user, password)
      .table(table)
      .load()
  }

  private def migrate(flyway: Flyway)(implicit F: Sync[F]): F[Unit] = {
    if (config.migration.migrate) F.delay(flyway.migrate()).as(()) else F.pure(())
  }
}

object MigrationManager {

  def apply[F[_]: Async](config: DatabaseConfig): MigrationManager[F] = new MigrationManager(config)
}
