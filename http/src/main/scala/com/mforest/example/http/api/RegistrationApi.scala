package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.RegistrationApiDoc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.registration.RegistrationService
import org.http4s.HttpRoutes

final class RegistrationApi[F[_]: Sync: ContextShift](registrationService: RegistrationService[F])
    extends Api[F]
    with RegistrationApiDoc {

  override def routes: HttpRoutes[F] = registerUser

  private val registerUser: HttpRoutes[F] = registerUserEndpoint.toHandleRoutes { request =>
    validate(request)
      .map(_.toDto)
      .flatMap(registrationService.register)
      .bimap(StatusResponse.fail, StatusResponse.ok)
  }
}

object RegistrationApi {

  def apply[F[_]: Sync: ContextShift](userService: RegistrationService[F]): RegistrationApi[F] =
    new RegistrationApi(userService)
}
