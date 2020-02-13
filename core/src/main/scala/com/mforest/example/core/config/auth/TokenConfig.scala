package com.mforest.example.core.config.auth

import scala.concurrent.duration.FiniteDuration

final case class TokenConfig(expiryDuration: FiniteDuration, maxIdle: Option[FiniteDuration])
