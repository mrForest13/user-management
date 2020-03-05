package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.response.StatusResponse.{Fail, Ok}
import com.mforest.example.http.token.BearerToken
import org.http4s.{EntityBody, HttpRoutes}
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.TapirHttp4sServer

import scala.language.implicitConversions

private[http] trait HttpServerSupport extends TapirHttp4sServer {
  this: ErrorHandlerSupport with HttpOptionsSupport =>

  type HttpEndpoint[I, O, F[_]] = Endpoint[I, Fail[Error], Ok[O], EntityBody[F]]

  implicit class RichHttpEndpoint[F[_]: Sync: ContextShift, I, O](endpoint: HttpEndpoint[I, O, F]) {

    def toHttpRoutes(logic: I => EitherT[F, Error, O]): HttpRoutes[F] = {
      endpoint.toRoutes(
        logic.andThen(
          handleError(_)
            .leftMap(StatusResponse.Fail(_))
            .map(StatusResponse.Ok(_))
        )
      )
    }
  }

  type AuthHttpEndpoint[I, O, F[_]] = Endpoint[I, Fail[Error], (BearerToken, Ok[O]), EntityBody[F]]

  implicit class RichAuthHttpEndpoint[F[_]: Sync: ContextShift, I, O](endpoint: AuthHttpEndpoint[I, O, F]) {

    def toAuthHttpRoutes(logic: I => EitherT[F, Error, (BearerToken, O)]): HttpRoutes[F] = {
      endpoint.toRoutes {
        logic.andThen {
          handleError(_)
            .leftMap(StatusResponse.fail)
            .map(toResponse)
        }
      }
    }

    private def toResponse(tuple: (BearerToken, O)): (BearerToken, Ok[O]) = tuple match {
      case (token, response) => token -> StatusResponse.ok(response)
    }
  }

  private implicit def toEither[F[_], L, R](eitherT: EitherT[F, L, R]): F[Either[L, R]] = eitherT.value
}
