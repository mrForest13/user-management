package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.model.Pagination
import com.mforest.example.core.permissions.Permissions
import com.mforest.example.http.Api
import com.mforest.example.http.doc.UserApiDoc
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.user.UserService
import org.http4s.HttpRoutes

class UserApi[F[_]: Sync: ContextShift](userService: UserService[F])(implicit authService: AuthService[F])
    extends Api[F]
    with UserApiDoc {

  override def routes: HttpRoutes[F] = addPermission <+> revokePermission <+> findUsers

  private val addPermission: HttpRoutes[F] = addPermissionEndpoint.toHandleRoutes {
    case (userId, permissionId, token) =>
      hasPermission(token, Permissions.USER_MANAGEMENT_ADD_PERMISSION_FOR_USERS) { () =>
        userService.addPermission(userId, permissionId)
      }
  }

  private val revokePermission: HttpRoutes[F] = revokePermissionEndpoint.toHandleRoutes {
    case (userId, permissionId, token) =>
      hasPermission(token, Permissions.USER_MANAGEMENT_REVOKE_PERMISSION_FOR_USERS) { () =>
        userService.revokePermission(userId, permissionId)
      }
  }

  private val findUsers: HttpRoutes[F] = findUsersEndpoint.toHandleRoutes {
    case (size, page, token) =>
      hasPermission(token, Permissions.USER_MANAGEMENT_GET_USERS) { () =>
        validate(Pagination(size, page)).flatMap(userService.getUsers)
      }
  }
}

object UserApi {

  def apply[F[_]: Sync: ContextShift](userService: UserService[F], authService: AuthService[F]): UserApi[F] = {
    implicit val authServiceImplicit: AuthService[F] = authService
    new UserApi[F](userService)
  }
}
