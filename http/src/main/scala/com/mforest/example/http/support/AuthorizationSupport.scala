package com.mforest.example.http.support

import cats.Id
import cats.data.EitherT
import cats.effect.Sync
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.response.StatusResponse.Fail
import com.mforest.example.http.token.BarerToken
import com.mforest.example.service.auth.AuthService
import io.chrisdavenport.fuuid.FUUID

trait AuthorizationSupport {

  def hasPermission[F[_]: Sync, R](token: String, permission: String)(
      logic: () => EitherT[F, Error, R]
  )(implicit authService: AuthService[F]): EitherT[F, Fail[Error], (BarerToken, StatusResponse.Ok[R])] = {
    checkPermissions(token, permission, logic)
      .leftMap(StatusResponse.fail)
      .map {
        case (token, result) =>
          token -> StatusResponse.ok(result)
      }
  }

  private def checkPermissions[F[_]: Sync, R](token: String, permission: String, logic: () => EitherT[F, Error, R])(
      implicit authService: AuthService[F]
  ): EitherT[F, Error, (BarerToken, R)] = {
    for {
      info   <- authService.authorize(token, permission)
      token  = BarerToken.apply[Id[FUUID]](info.authenticator)
      result <- logic.apply()
    } yield token -> result
  }
}
