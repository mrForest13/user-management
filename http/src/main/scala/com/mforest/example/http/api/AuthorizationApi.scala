package com.mforest.example.http.api

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import cats.implicits.catsStdShowForString
import com.mforest.example.http.Api
import com.mforest.example.http.doc.AuthorizationApiDoc
import com.mforest.example.http.support.AuthorizationSupport
import com.mforest.example.service.auth.AuthService
import org.http4s.HttpRoutes

final class AuthorizationApi[F[_]: Sync: ContextShift](val authService: AuthService[F])
    extends Api[F]
    with AuthorizationSupport[F]
    with AuthorizationApiDoc {

  override def routes: HttpRoutes[F] = validatePermission

  private val validatePermission: HttpRoutes[F] = validatePermissionEndpoint.toHandleRoutes {
    case (permission, token) =>
      authorize(token, permission) { authInfo =>
        EitherT.rightT(authInfo)
      }
  }
}

object AuthorizationApi {

  def apply[F[_]: Sync: ContextShift](authService: AuthService[F]): AuthorizationApi[F] = {
    new AuthorizationApi[F](authService)
  }
}
