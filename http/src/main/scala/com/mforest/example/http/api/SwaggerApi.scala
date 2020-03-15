package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.config.swagger.SwaggerConfig
import com.mforest.example.http.Api
import org.http4s.HttpRoutes
import sttp.tapir.swagger.http4s.SwaggerHttp4s

final class SwaggerApi[F[_]: Sync: ContextShift](yaml: String, config: SwaggerConfig)
    extends SwaggerHttp4s(yaml, config.contextPath, config.yamlName, config.redirectQuery)
    with Api[F] {

  override def routes: HttpRoutes[F] = super.routes[F]
}

object SwaggerApi {

  def apply[F[_]: Sync: ContextShift](yaml: String, config: SwaggerConfig): SwaggerApi[F] = {
    new SwaggerApi(yaml, config)
  }
}
