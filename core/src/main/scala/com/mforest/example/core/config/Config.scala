package com.mforest.example.core.config

import com.mforest.example.core.config.app.AppConfig
import com.mforest.example.core.config.auth.AuthConfig
import com.mforest.example.core.config.db.DatabaseConfig
import com.mforest.example.core.config.http.HttpConfig

final case class Config(
    app: AppConfig,
    auth: AuthConfig,
    http: HttpConfig,
    database: DatabaseConfig,
    swagger: Map[String, Seq[String]]
)
