package com.mforest.example.http.api

import cats.SemigroupK.nonInheritedOps._
import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.model.Pagination
import com.mforest.example.http.Api
import com.mforest.example.http.doc.UserApiDoc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.user.UserService
import org.http4s.HttpRoutes

class UserApi[F[_]: Sync: ContextShift](userService: UserService[F]) extends Api[F] with UserApiDoc {

  override def routes: HttpRoutes[F] = addPermission <+> revokePermission <+> findUsers

  private val addPermission: HttpRoutes[F] = addPermissionEndpoint.toHandleRoutes {
    case (userId, permissionId) =>
      userService
        .addPermission(userId, permissionId)
        .bimap(StatusResponse.fail, StatusResponse.ok)
  }

  private val revokePermission: HttpRoutes[F] = revokePermissionEndpoint.toHandleRoutes {
    case (userId, permissionId) =>
      userService
        .revokePermission(userId, permissionId)
        .bimap(StatusResponse.fail, StatusResponse.ok)
  }

  private val findUsers: HttpRoutes[F] = findUsersEndpoint.toHandleRoutes { pagination =>
    validate(Pagination(pagination))
      .flatMap(userService.getUsers)
      .bimap(StatusResponse.fail, StatusResponse.ok)
  }
}

object UserApi {

  def apply[F[_]: Sync: ContextShift](userService: UserService[F]): UserApi[F] = new UserApi[F](userService)
}
