package com.mforest.example.http.api

import cats.Functor.ops.toAllFunctorOps
import cats.Id
import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.AuthenticationApiDoc
import com.mforest.example.http.form.LoginForm
import com.mforest.example.http.support.AuthorizationSupport
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.login.LoginService
import io.chrisdavenport.fuuid.FUUID
import org.http4s.HttpRoutes

final class AuthenticationApi[F[_]: Sync: ContextShift](loginService: LoginService[F], val authService: AuthService[F])
    extends Api[F]
    with AuthorizationSupport[F]
    with AuthenticationApiDoc {

  private val loginMsg: String  = "Login succeeded!"
  private val logoutMsg: String = "Logout succeeded!"

  override def routes: HttpRoutes[F] = loginUser <+> logoutUser

  private val loginUser: HttpRoutes[F] = loginUserEndpoint.toAuthHttpRoutes { credentials =>
    validate(LoginForm(credentials))
      .map(_.toDto)
      .flatMap(loginService.login)
      .semiflatMap(authService.create)
      .map(BearerToken.apply[Id[FUUID]])
      .map(_ -> loginMsg)
  }

  private val logoutUser: HttpRoutes[F] = logoutUserEndpoint.toHttpRoutes { token =>
    authService
      .validateAndRenew(token)
      .map(_.authenticator)
      .semiflatMap(authService.discard)
      .as(logoutMsg)
  }
}

object AuthenticationApi {

  def apply[F[_]: Sync: ContextShift](
      loginService: LoginService[F],
      authService: AuthService[F]
  ): AuthenticationApi[F] = {
    new AuthenticationApi(loginService, authService)
  }
}
