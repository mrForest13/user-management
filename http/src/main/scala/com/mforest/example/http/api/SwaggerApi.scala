package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import org.http4s.HttpRoutes
import sttp.tapir.swagger.http4s.SwaggerHttp4s

final class SwaggerApi[F[_]: Sync: ContextShift](yaml: String, swagger: Map[String, Seq[String]])
    extends SwaggerHttp4s(yaml = yaml, redirectQuery = swagger)
    with Api[F] {

  override def routes: HttpRoutes[F] = super.routes[F]
}

object SwaggerApi {

  def apply[F[_]: Sync: ContextShift](yaml: String, swagger: Map[String, Seq[String]]): SwaggerApi[F] = {
    new SwaggerApi(yaml, swagger)
  }
}
