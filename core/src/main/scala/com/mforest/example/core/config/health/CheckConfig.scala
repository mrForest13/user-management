package com.mforest.example.core.config.health

import scala.concurrent.duration.FiniteDuration

final case class CheckConfig(name: String, timeout: FiniteDuration)
