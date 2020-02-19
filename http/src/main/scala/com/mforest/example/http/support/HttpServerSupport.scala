package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse.Fail
import org.http4s.{EntityBody, HttpRoutes}
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.TapirHttp4sServer

trait HttpServerSupport extends TapirHttp4sServer {
  this: ErrorHandlerSupport with HttpOptionsSupport =>

  implicit class RichHttpEndpoint[F[_]: Sync, I, O](endpoint: Endpoint[I, Fail[Error], O, EntityBody[F]]) {

    def toHandleRoutes(logic: I => EitherT[F, Fail[Error], O])(implicit fcs: ContextShift[F]): HttpRoutes[F] = {
      endpoint.toRoutes(logic.andThen(handleError(_).value))
    }
  }
}
