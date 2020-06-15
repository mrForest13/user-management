package com.mforest.example.http.support

import cats.Show
import cats.data.EitherT
import cats.effect.Sync
import com.mforest.example.core.error.Error
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.model.SessionInfo

private[http] trait AuthorizationSupport[F[_]] {

  def authService: AuthService[F]

  type Logic[R] = SessionInfo => EitherT[F, Error, R]

  type AuthorizeResult[R] = EitherT[F, Error, (BearerToken, R)]

  def authorize[P: Show, R](token: String, permission: P)(logic: Logic[R])(implicit S: Sync[F]): AuthorizeResult[R] = {
    for {
      info   <- authService.authorize(token, permission)
      token  = BearerToken(info.authenticator)
      result <- logic(info)
    } yield token -> result
  }
}
