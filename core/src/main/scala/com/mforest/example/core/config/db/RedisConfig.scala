package com.mforest.example.core.config.db

final case class RedisConfig(host: String, port: Int, password: Option[String])
