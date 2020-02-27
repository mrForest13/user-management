package com.mforest.example.db

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

final class Database[F[_]: Async: ContextShift](config: DatabaseConfig) {

  def transactor(connectEC: ExecutionContext, blocker: Blocker): Resource[F, HikariTransactor[F]] = {
    HikariTransactor.newHikariTransactor[F](
      driverClassName = config.driver,
      url = config.postgresUrl,
      user = config.user,
      pass = config.password,
      connectEC = connectEC,
      blocker = blocker
    )
  }
}

object Database {

  def apply[F[_]: Async: ContextShift](config: DatabaseConfig): Database[F] = new Database(config)
}
