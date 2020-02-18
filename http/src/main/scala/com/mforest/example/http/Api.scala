package com.mforest.example.http

import cats.data.EitherT
import com.mforest.example.http.support.{ErrorHandlerSupport, HttpOptionsSupport, HttpServerSupport, ValidationSupport}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import scala.language.implicitConversions

trait Api[F[_]]
    extends Http4sDsl[F]
    with ErrorHandlerSupport
    with HttpOptionsSupport
    with HttpServerSupport
    with ValidationSupport {

  def routes: HttpRoutes[F]

  implicit def toEither[A, B](eitherT: EitherT[F, A, B]): F[Either[A, B]] = eitherT.value
}
