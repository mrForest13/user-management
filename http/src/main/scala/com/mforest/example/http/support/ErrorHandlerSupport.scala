package com.mforest.example.http.support

import java.sql.SQLException

import cats.data.EitherT
import cats.data.EitherT.right
import cats.effect.Sync
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxEitherId, toFunctorOps}
import com.mforest.example.core.error.Error
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

private[http] trait ErrorHandlerSupport {

  def handleError[F[_]: Sync, R](either: EitherT[F, Error, R]): EitherT[F, Error, R] = {
    right(Slf4jLogger.create[F]).flatMapF { logger =>
      either.value.handleErrorWith { th =>
        logger
          .error(th)(th.getMessage)
          .as(handle(th).asLeft[R])
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
