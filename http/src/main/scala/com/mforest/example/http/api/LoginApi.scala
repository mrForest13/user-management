package com.mforest.example.http.api

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.error.Error
import com.mforest.example.http.Api
import com.mforest.example.http.doc.LoginApiDoc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import org.http4s.HttpRoutes
import tsec.common.SecureRandomId

class LoginApi[F[_]: Sync: ContextShift] extends Api[F] with LoginApiDoc {

  override def routes: HttpRoutes[F] = registerUser

  private val registerUser: HttpRoutes[F] = loginUserEndpoint.toRoutes { credentials =>
    EitherT
      .rightT[F, Error](credentials.toString)
      .bimap(StatusResponse.fail, StatusResponse.ok)
      .map((BarerToken(SecureRandomId.Strong.generate), _))
  }
}

object LoginApi {

  def apply[F[_]: Sync: ContextShift](): LoginApi[F] =
    new LoginApi()
}
