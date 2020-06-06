package com.mforest.example.core.config.health

final case class HealthCheckConfig(database: CheckConfig, cache: CheckConfig)
