package com.mforest.example.core.config.db

final case class PostgresConfig(
    host: String,
    port: Int,
    schema: String,
    user: String,
    password: String,
    driver: String,
    connectPoolSize: Int,
    maxConnectionsPoolSize: Int,
    maxConnectionsPoolName: String,
    properties: Map[String, String]
) {

  val postgresUrl: String = s"jdbc:postgresql://$host:$port/$schema"
}
