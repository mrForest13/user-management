package com.mforest.example.core.config.http

import scala.concurrent.duration.Duration

final case class HttpConfig(host: String, port: Int, serverPoolSize: Int, serverTimeout: Duration)
