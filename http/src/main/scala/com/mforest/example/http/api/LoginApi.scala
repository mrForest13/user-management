package com.mforest.example.http.api

import cats.data.EitherT
import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.LoginApiDoc
import com.mforest.example.core.error.Error
import org.http4s.HttpRoutes

class LoginApi[F[_]: Sync: ContextShift] extends Api[F] with LoginApiDoc {

  override def routes: HttpRoutes[F] = registerUser

  private val registerUser: HttpRoutes[F] = loginUserEndpoint.toRoutes { credentials =>
    complete {
      EitherT.rightT[F, Error](credentials.toString)
    }
  }
}

object LoginApi {

  def apply[F[_]: Sync: ContextShift](): LoginApi[F] =
    new LoginApi()
}
