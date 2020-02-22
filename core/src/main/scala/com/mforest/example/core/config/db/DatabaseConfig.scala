package com.mforest.example.core.config.db

final case class DatabaseConfig(
    host: String,
    port: Int,
    schema: String,
    user: String,
    password: String,
    driver: String,
    poolSize: Int,
    migrate: Boolean,
    migrationTable: String
) {

  val postgresUrl: String = s"jdbc:postgresql://$host:$port/$schema"
}
