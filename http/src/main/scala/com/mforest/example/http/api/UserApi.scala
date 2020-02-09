package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.http.Api
import com.mforest.example.http.doc.UserApiDoc
import com.mforest.example.service.user.UserService
import org.http4s.HttpRoutes

class UserApi[F[_]: Sync: ContextShift](userService: UserService[F]) extends Api[F] with UserApiDoc {

  def routes: HttpRoutes[F] = addUser

  private val addUser: HttpRoutes[F] = addUserEndpoint.toRoutes { req =>
    complete {
      validate(req).flatMap { user =>
        userService.createUser(user)
      }
    }
  }
}

object UserApi {

  def apply[F[_]: Sync: ContextShift](userService: UserService[F]): UserApi[F] =
    new UserApi(userService)
}
