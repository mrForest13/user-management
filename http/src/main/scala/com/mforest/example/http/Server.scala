package com.mforest.example.http

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import com.mforest.example.core.config.Config
import org.http4s.HttpRoutes
import org.http4s.server.{Server => BlazeServer}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.KleisliSyntax

class Server[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, routes: HttpRoutes[F]) extends KleisliSyntax {

  def resource: Resource[F, BlazeServer[F]] = {
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withBanner(config.app.stripBanner)
      .withHttpApp(routes.orNotFound)
      .resource
  }
}

object Server {

  def apply[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, routes: HttpRoutes[F]): Server[F] = {
    new Server(config, routes)
  }
}
