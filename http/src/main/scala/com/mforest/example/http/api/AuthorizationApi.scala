package com.mforest.example.http.api

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.AuthorizationApiDoc
import com.mforest.example.service.auth.AuthService
import org.http4s.HttpRoutes

class AuthorizationApi[F[_]: Sync: ContextShift](implicit authService: AuthService[F])
    extends Api[F]
    with AuthorizationApiDoc {

  private val validateMsg: String = "The user has the required permission!"

  override def routes: HttpRoutes[F] = validatePermission

  private val validatePermission: HttpRoutes[F] = validatePermissionEndpoint.toHandleRoutes {
    case (permission, token) =>
      hasPermission(token, permission) { () =>
        EitherT.rightT(validateMsg)
      }
  }
}

object AuthorizationApi {

  def apply[F[_]: Sync: ContextShift](authService: AuthService[F]): AuthorizationApi[F] = {
    implicit val authServiceImplicit: AuthService[F] = authService
    new AuthorizationApi[F]
  }
}
