package com.mforest.example.http

import com.mforest.example.http.handle.ResponseHandler
import com.mforest.example.http.validation.{HttpServerOptions, ValidationSupport}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.server.http4s.TapirHttp4sServer

trait Api[F[_]]
    extends Http4sDsl[F]
    with TapirHttp4sServer
    with HttpServerOptions
    with ResponseHandler
    with ValidationSupport {

  def routes: HttpRoutes[F]
}
