package com.mforest.example.core.config.db

final case class DatabaseConfig(
    host: String,
    port: Int,
    schema: String,
    user: String,
    password: String,
    driver: String,
    poolSize: Int
) {

  val url: String = s"jdbc:postgresql://$host:$port/$schema"
}
