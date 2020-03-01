package com.mforest.example.db

import java.util.Properties

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.mforest.example.core.config.db.PostgresConfig
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor

import scala.collection.convert.AsJavaExtensions
import scala.concurrent.ExecutionContext

final class Database[F[_]: Async: ContextShift](config: PostgresConfig) extends AsJavaExtensions {

  val databaseConfig: HikariConfig = {
    val hikari     = new HikariConfig()
    val properties = new Properties()

    properties.putAll(config.properties.asJava)

    hikari.setUsername(config.user)
    hikari.setPassword(config.password)
    hikari.setJdbcUrl(config.postgresUrl)
    hikari.setDriverClassName(config.driver)
    hikari.setDataSourceProperties(properties)
    hikari.setPoolName(config.maxConnectionsPoolName)
    hikari.setMaximumPoolSize(config.maxConnectionsPoolSize)

    hikari
  }

  def transactor(connectEC: ExecutionContext, blocker: Blocker): Resource[F, HikariTransactor[F]] = {
    HikariTransactor.fromHikariConfig[F](
      hikariConfig = databaseConfig,
      connectEC = connectEC,
      blocker = blocker
    )
  }
}

object Database {

  def apply[F[_]: Async: ContextShift](config: PostgresConfig): Database[F] = new Database(config)
}
