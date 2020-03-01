package com.mforest.example.http.support

import java.sql.SQLException

import cats.data.EitherT
import cats.data.EitherT.right
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeError, toFunctorOps}
import cats.syntax.EitherSyntax
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse.Fail
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait ErrorHandlerSupport extends EitherSyntax {

  def handleError[F[_]: Sync, R](either: EitherT[F, Fail[Error], R]): EitherT[F, Fail[Error], R] = {
    right(Slf4jLogger.create[F]).flatMapF { logger =>
      either.value.handleErrorWith { th =>
        logger
          .error(th)(th.getMessage)
          .as(Fail(handle(th)).asLeft[R])
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
