package com.mforest.example.http

import cats.SemigroupK.ToSemigroupKOps
import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import com.mforest.example.core.config.Config
import com.mforest.example.http.response.StatusResponse
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Server => BlazeServer}
import org.http4s.syntax.KleisliSyntax

final class Server[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, apis: Seq[Api[F]])
    extends Http4sDsl[F]
    with KleisliSyntax
    with ToSemigroupKOps {

  private val notFoundMsg: String = "Not Found"

  def resource: Resource[F, BlazeServer[F]] = {
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withBanner(config.app.stripBanner)
      .withHttpApp(routes.orNotFound)
      .withNio2(isNio2 = true)
      .resource
  }

  private def routes: HttpRoutes[F] = {
    apis.map(_.routes).foldRight(notFound)(_ <+> _)
  }

  private def notFound: HttpRoutes[F] = HttpRoutes.of {
    case _ => NotFound(StatusResponse.fail(notFoundMsg).asJson.spaces2)
  }
}

object Server {

  def apply[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, apis: Seq[Api[F]]): Server[F] = {
    new Server(config, apis)
  }
}
