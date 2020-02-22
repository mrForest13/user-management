package com.mforest.example.http.api

import cats.Id
import cats.SemigroupK.nonInheritedOps._
import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.AuthenticationApiDoc
import com.mforest.example.http.form.LoginForm
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.login.LoginService
import io.chrisdavenport.fuuid.FUUID
import org.http4s.HttpRoutes
import tsec.authentication.TSecBearerToken

class AuthenticationApi[F[_]: Sync: ContextShift, V](
    loginService: LoginService[F],
    authService: AuthService[F, Id[FUUID], V, TSecBearerToken[Id[FUUID]]]
) extends Api[F]
    with AuthenticationApiDoc {

  private val loginMsg: String  = "Login succeeded!"
  private val logoutMsg: String = "Logout succeeded!"

  override def routes: HttpRoutes[F] = loginUser <+> logoutUser

  private val loginUser: HttpRoutes[F] = loginUserEndpoint.toHandleRoutes { credentials =>
    validate(LoginForm(credentials))
      .map(_.toDto)
      .flatMap(loginService.login)
      .semiflatMap(authService.createToken)
      .map(BarerToken.apply[Id[FUUID]])
      .bimap(StatusResponse.fail, _ -> StatusResponse.Ok(loginMsg))
  }

  private val logoutUser: HttpRoutes[F] = logoutUserEndpoint.toHandleRoutes { token =>
    authService
      .validateToken(token)
      .map(_.authenticator)
      .semiflatMap(authService.discardToken)
      .bimap(StatusResponse.fail, _ => StatusResponse.Ok(logoutMsg))
  }
}

object AuthenticationApi {

  def apply[F[_]: Sync: ContextShift, V](
      loginService: LoginService[F],
      authService: AuthService[F, Id[FUUID], V, TSecBearerToken[Id[FUUID]]]
  ): AuthenticationApi[F, V] = {
    new AuthenticationApi(loginService, authService)
  }
}
