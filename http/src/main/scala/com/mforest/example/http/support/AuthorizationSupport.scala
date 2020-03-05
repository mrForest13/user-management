package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.Sync
import cats.{Id, Show}
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.response.StatusResponse.Fail
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.model.SessionInfo
import io.chrisdavenport.fuuid.FUUID

private[http] trait AuthorizationSupport[F[_]] {

  def authService: AuthService[F]

  type Logic[R] = SessionInfo => EitherT[F, Error, R]

  type AuthorizeResult[R] = EitherT[F, Fail[Error], (BearerToken, StatusResponse.Ok[R])]

  def authorize[P: Show, R](token: String, permission: P)(logic: Logic[R])(implicit S: Sync[F]): AuthorizeResult[R] = {
    check(token, permission, logic)
      .leftMap(StatusResponse.fail)
      .map {
        case (token, result) =>
          token -> StatusResponse.ok(result)
      }
  }

  type CheckResult[R] = EitherT[F, Error, (BearerToken, R)]

  private def check[P: Show, R](token: String, permission: P, logic: Logic[R])(implicit S: Sync[F]): CheckResult[R] = {
    for {
      info   <- authService.authorize(token, permission)
      token  = BearerToken.apply[Id[FUUID]](info.authenticator)
      result <- logic.apply(info)
    } yield token -> result
  }
}
