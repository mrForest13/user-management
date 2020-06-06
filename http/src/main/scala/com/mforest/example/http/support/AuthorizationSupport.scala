package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.Sync
import cats.{Id, Show}
import com.mforest.example.core.error.Error
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.model.SessionInfo
import io.chrisdavenport.fuuid.FUUID

private[http] trait AuthorizationSupport[F[_]] {

  def authService: AuthService[F]

  type Logic[R] = SessionInfo => EitherT[F, Error, R]

  type AuthorizeResult[R] = EitherT[F, Error, (BearerToken, R)]

  def authorize[P: Show, R](token: String, permission: P)(logic: Logic[R])(implicit S: Sync[F]): AuthorizeResult[R] = {
    for {
      info   <- authService.authorize(token, permission)
      token  = BearerToken.apply[Id[FUUID]](info.authenticator)
      result <- logic.apply(info)
    } yield token -> result
  }
}
