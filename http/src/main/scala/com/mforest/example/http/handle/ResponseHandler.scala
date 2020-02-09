package com.mforest.example.http.handle

import java.sql.SQLException

import cats.data.EitherT
import cats.data.EitherT.right
import cats.effect.Sync
import cats.implicits._
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait ResponseHandler {

  type Response[F[_], R] = F[Either[StatusResponse.Fail[Error], StatusResponse.Ok[R]]]

  def complete[F[_]: Sync, R](either: EitherT[F, Error, R]): Response[F, R] = {
    handleError(either)
      .bimap(StatusResponse.fail, StatusResponse.ok)
      .value
  }

  private def handleError[F[_]: Sync, R](either: EitherT[F, Error, R]): EitherT[F, Error, R] = {
    right(Slf4jLogger.create[F]).flatMapF { logger =>
      either.value.handleErrorWith { th =>
        logger
          .error(th)(th.getMessage)
          .map(_ => handle(th).asLeft)
      }
    }
  }

  private val handle = (th: Throwable) => {
    th match {
      case _: SQLException => Error.UnavailableError("The server is currently unavailable!")
      case _: Throwable    => Error.InternalError("There was an internal server error!")
    }
  }
}
