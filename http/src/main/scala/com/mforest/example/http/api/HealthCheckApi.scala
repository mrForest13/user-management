package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.HealthCheckDoc
import com.mforest.example.service.health.HealthCheckService
import org.http4s.HttpRoutes

final class HealthCheckApi[F[_]: Sync: ContextShift](healthCheckService: HealthCheckService[F])
    extends Api[F]
    with HealthCheckDoc {

  override def routes: HttpRoutes[F] = healthCheck

  private val healthCheck = healthCheckEndpoint.toHttpRoutes { _ =>
    healthCheckService.check
  }
}

object HealthCheckApi {

  def apply[F[_]: Sync: ContextShift](healthCheckService: HealthCheckService[F]): HealthCheckApi[F] = {
    new HealthCheckApi(healthCheckService)
  }
}
