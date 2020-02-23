package com.mforest.example.http

import cats.SemigroupK.ToSemigroupKOps
import com.mforest.example.http.support.{
  AuthorizationSupport,
  ErrorHandlerSupport,
  HttpOptionsSupport,
  HttpServerSupport,
  ValidationSupport
}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

trait Api[F[_]]
    extends Http4sDsl[F]
    with ToSemigroupKOps
    with ValidationSupport
    with ErrorHandlerSupport
    with AuthorizationSupport
    with HttpOptionsSupport
    with HttpServerSupport {

  def routes: HttpRoutes[F]
}
