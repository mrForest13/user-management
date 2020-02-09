package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.RegistrationApiDoc
import com.mforest.example.service.registration.RegistrationService
import org.http4s.HttpRoutes

class RegistrationApi[F[_]: Sync: ContextShift](registrationService: RegistrationService[F])
    extends Api[F]
    with RegistrationApiDoc {

  def routes: HttpRoutes[F] = registerUser

  private val registerUser: HttpRoutes[F] = registerUserEndpoint.toRoutes { req =>
    complete {
      validate(req).flatMap { user =>
        registrationService.register(user)
      }
    }
  }
}

object RegistrationApi {

  def apply[F[_]: Sync: ContextShift](userService: RegistrationService[F]): RegistrationApi[F] =
    new RegistrationApi(userService)
}
