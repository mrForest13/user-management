package com.mforest.example.db.migration

import cats.effect.Sync
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

final class MigrationManager[F[_]: Sync](config: DatabaseConfig) {

  private val url: String      = config.postgres.postgresUrl
  private val user: String     = config.postgres.user
  private val table: String    = config.migration.migrationTable
  private val password: String = config.postgres.password

  def migrate(transactor: HikariTransactor[F]): Unit = migrate {
    flyway(transactor)
  }

  def migrate(): Unit = migrate {
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

  private def migrate(flyway: Flyway): Unit = {
    if (config.migration.migrate) {
      flyway.migrate(); ()
    } else {
      ()
    }
  }
}

object MigrationManager {

  def apply[F[_]: Sync](config: DatabaseConfig): MigrationManager[F] = new MigrationManager(config)
}
