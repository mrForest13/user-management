package com.mforest.example.http

import cats.SemigroupK.nonInheritedOps._
import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import com.mforest.example.core.config.Config
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Server => BlazeServer}
import org.http4s.syntax.KleisliSyntax

class Server[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, apis: Seq[Api[F]]) extends KleisliSyntax {

  def resource: Resource[F, BlazeServer[F]] = {
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withBanner(config.app.stripBanner)
      .withHttpApp(prepareRoutes.orNotFound)
      .resource
  }

  private def prepareRoutes: HttpRoutes[F] = {
    apis.map(_.routes).foldLeft(HttpRoutes.empty[F])(_ <+> _)
  }
}

object Server {

  def apply[F[_]: ContextShift: ConcurrentEffect: Timer](config: Config, apis: Api[F]*): Server[F] = {
    new Server(config, apis)
  }
}
