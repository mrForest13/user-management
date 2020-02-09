package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import org.http4s.HttpRoutes
import sttp.tapir.swagger.http4s.SwaggerHttp4s

class SwaggerApi[F[_]: Sync: ContextShift](yaml: String) extends SwaggerHttp4s(yaml) with Api[F] {

  override def routes: HttpRoutes[F] = super.routes[F]
}

object SwaggerApi {

  def apply[F[_]: Sync: ContextShift](yaml: String): SwaggerApi[F] = new SwaggerApi(yaml)
}
