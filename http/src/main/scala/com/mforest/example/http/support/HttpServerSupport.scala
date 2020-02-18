package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import org.http4s.{EntityBody, HttpRoutes}
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.{EndpointToHttp4sServer, TapirHttp4sServer}
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse.Fail

trait HttpServerSupport extends TapirHttp4sServer {
  this: ErrorHandlerSupport with HttpOptionsSupport =>

  implicit class RichHttpEndpoint[F[_]: Sync, I, O](e: Endpoint[I, Fail[Error], O, EntityBody[F]]) {

    def toRoutes(logic: I => EitherT[F, Fail[Error], O])(implicit fcs: ContextShift[F]): HttpRoutes[F] = {
      val logicWithHandler = logic.andThen(handleError(_).value)
      new EndpointToHttp4sServer(serverOptions).toRoutes(e.serverLogic(logicWithHandler))
    }
  }
}
