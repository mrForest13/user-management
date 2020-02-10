package com.mforest.example.http

import cats.data.EitherT
import com.mforest.example.http.handle.ErrorHandler
import com.mforest.example.http.support.{HttpOptionsSupport, ValidationSupport}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.server.http4s.TapirHttp4sServer

import scala.language.implicitConversions

trait Api[F[_]]
    extends Http4sDsl[F]
    with TapirHttp4sServer
    with HttpOptionsSupport
    with ErrorHandler
    with ValidationSupport {

  def routes: HttpRoutes[F]

  implicit def toEither[A, B](eitherT: EitherT[F, A, B]): F[Either[A, B]] = eitherT.value
}
