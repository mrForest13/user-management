package com.mforest.example.http

import cats.SemigroupK.ToSemigroupKOps
import com.mforest.example.http.support.{ErrorHandlerSupport, HttpOptionsSupport, HttpServerSupport, ValidationSupport}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

private[http] trait Api[F[_]]
    extends Http4sDsl[F]
    with ToSemigroupKOps
    with ValidationSupport
    with ErrorHandlerSupport
    with HttpOptionsSupport
    with HttpServerSupport {

  def routes: HttpRoutes[F]
}
