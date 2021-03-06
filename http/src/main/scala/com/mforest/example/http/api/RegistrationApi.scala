package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.RegistrationDoc
import com.mforest.example.service.registration.RegistrationService
import org.http4s.HttpRoutes

final class RegistrationApi[F[_]: Sync: ContextShift](registrationService: RegistrationService[F])
    extends Api[F]
    with RegistrationDoc {

  override def routes: HttpRoutes[F] = registerUser

  private val registerUser: HttpRoutes[F] = registerUserEndpoint.toHttpRoutes { request =>
    validate(request)
      .map(_.toDto)
      .flatMap(registrationService.register)
  }
}

object RegistrationApi {

  def apply[F[_]: Sync: ContextShift](userService: RegistrationService[F]): RegistrationApi[F] =
    new RegistrationApi(userService)
}
