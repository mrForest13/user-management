package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.LoginApiDoc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import com.mforest.example.service.login.LoginService
import com.mforest.example.service.model.Credentials
import org.http4s.HttpRoutes
import tsec.common.SecureRandomId

class LoginApi[F[_]: Sync: ContextShift](loginService: LoginService[F]) extends Api[F] with LoginApiDoc {

  override def routes: HttpRoutes[F] = registerUser

  private val registerUser: HttpRoutes[F] = loginUserEndpoint.toRoutes { credentials =>
    loginService
      .login(Credentials(credentials.username, credentials.password.get))
      .map(_.toString())
      .bimap(StatusResponse.fail, StatusResponse.ok)
      .map((BarerToken(SecureRandomId.Strong.generate), _))
  }
}

object LoginApi {

  def apply[F[_]: Sync: ContextShift](loginService: LoginService[F]): LoginApi[F] = {
    new LoginApi(loginService)
  }
}
