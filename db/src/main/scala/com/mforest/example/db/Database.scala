package com.mforest.example.db

import cats.effect.{Async, ContextShift, Resource}
import com.mforest.example.core.config.db.DatabaseConfig
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

class Database[F[_]: Async: ContextShift](config: DatabaseConfig) {

  def transactor(connEc: ExecutionContext, txnEc: ExecutionContext): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      driverClassName = config.driver,
      url = config.postgresUrl,
      user = config.user,
      pass = config.password,
      connectEC = connEc,
      transactEC = txnEc
    )
}

object Database {

  def apply[F[_]: Async: ContextShift](config: DatabaseConfig): Database[F] = new Database(config)
}
