package com.mforest.example.http.validation

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.response.StatusResponse
import sttp.model.StatusCode
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.server.http4s.{Http4sServerOptions, TapirHttp4sServer}
import sttp.tapir.server.{DecodeFailureHandling, DefaultDecodeFailureHandler, ServerDefaults}
import sttp.tapir.{EndpointOutput, Tapir}

trait HttpServerOptions extends Tapir with TapirJsonCirce {
  this: TapirHttp4sServer =>

  private val failureOutput: EndpointOutput[(StatusCode, StatusResponse.Fail[String])] = {
    statusCode.and(jsonBody[StatusResponse.Fail[String]])
  }

  private val decodeFailureHandler: DefaultDecodeFailureHandler = {
    ServerDefaults.decodeFailureHandler.copy(response = failureResponse)
  }

  private def failureResponse(statusCode: StatusCode, message: String): DecodeFailureHandling =
    DecodeFailureHandling.response(failureOutput)((statusCode, StatusResponse.Fail(message)))

  implicit def serverOptions[F[_]: Sync: ContextShift]: Http4sServerOptions[F] = {
    Http4sServerOptions.default[F].copy(decodeFailureHandler = decodeFailureHandler)
  }
}
