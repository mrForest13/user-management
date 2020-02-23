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
    val result = for {
      info   <- authService.authorize(token, permission)
      token  = BarerToken.apply[Id[FUUID]](info.authenticator)
      result <- logic.apply()
    } yield token -> result

    result
      .leftMap(StatusResponse.fail)
      .map(t => t._1 -> StatusResponse.ok(t._2))
  }
}
