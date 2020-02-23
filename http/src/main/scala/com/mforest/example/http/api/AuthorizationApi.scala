package com.mforest.example.http.api

import cats.data.NonEmptyChain
import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.AuthorizationApiDoc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.dto.PermissionDto
import org.http4s.HttpRoutes

class AuthorizationApi[F[_]: Sync: ContextShift, I, A](authService: AuthService[F, I, NonEmptyChain[PermissionDto], A])
    extends Api[F]
    with AuthorizationApiDoc {

  private val validateMsg: String = "The user has the required permission!"

  override def routes: HttpRoutes[F] = validatePermission

  private val validatePermission: HttpRoutes[F] = validatePermissionEndpoint.toHandleRoutes {
    case (permission, token) =>
      authService
        .authorize(token, permission)
        .map(_ => validateMsg)
        .bimap(StatusResponse.fail, StatusResponse.ok)
  }
}

object AuthorizationApi {

  def apply[F[_]: Sync: ContextShift, I, A](
      authService: AuthService[F, I, NonEmptyChain[PermissionDto], A]
  ): AuthorizationApi[F, I, A] = {
    new AuthorizationApi(authService)
  }
}
