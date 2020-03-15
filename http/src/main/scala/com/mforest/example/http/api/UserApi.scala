package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.model.Pagination
import com.mforest.example.core.permission.Permissions
import com.mforest.example.http.Api
import com.mforest.example.http.doc.UserDoc
import com.mforest.example.http.support.AuthorizationSupport
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.user.UserService
import org.http4s.HttpRoutes

final class UserApi[F[_]: Sync: ContextShift](userService: UserService[F], val authService: AuthService[F])
    extends Api[F]
    with AuthorizationSupport[F]
    with UserDoc {

  override def routes: HttpRoutes[F] = addPermission <+> revokePermission <+> findUsers

  private val addPermission: HttpRoutes[F] = addPermissionEndpoint.toAuthHttpRoutes {
    case (userId, permissionId, token) =>
      authorize(token, Permissions.USER_MANAGEMENT_ADD_PERMISSION_FOR_USERS) { _ =>
        userService.addPermission(userId, permissionId)
      }
  }

  private val revokePermission: HttpRoutes[F] = revokePermissionEndpoint.toAuthHttpRoutes {
    case (userId, permissionId, token) =>
      authorize(token, Permissions.USER_MANAGEMENT_REVOKE_PERMISSION_FOR_USERS) { _ =>
        userService.revokePermission(userId, permissionId)
      }
  }

  private val findUsers: HttpRoutes[F] = findUsersEndpoint.toAuthHttpRoutes {
    case (size, page, token) =>
      authorize(token, Permissions.USER_MANAGEMENT_GET_USERS) { _ =>
        validate(Pagination(size, page)).flatMap(userService.getUsers)
      }
  }
}

object UserApi {

  def apply[F[_]: Sync: ContextShift](userService: UserService[F], authService: AuthService[F]): UserApi[F] = {
    new UserApi[F](userService, authService)
  }
}
