package com.mforest.example.http.api

import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.model.Pagination
import com.mforest.example.core.permission.Permissions
import com.mforest.example.http.Api
import com.mforest.example.http.doc.PermissionApiDoc
import com.mforest.example.http.support.AuthorizationSupport
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.permission.PermissionService
import org.http4s.HttpRoutes

final class PermissionApi[F[_]: Sync: ContextShift](service: PermissionService[F], val authService: AuthService[F])
    extends Api[F]
    with AuthorizationSupport[F]
    with PermissionApiDoc {

  override def routes: HttpRoutes[F] = addPermission <+> findUserPermissions <+> findPermissions

  private val addPermission: HttpRoutes[F] = addPermissionEndpoint.toAuthHttpRoutes {
    case (token, request) =>
      authorize(token, Permissions.USER_MANAGEMENT_ADD_PERMISSION) { _ =>
        validate(request)
          .map(_.toDto)
          .flatMap(service.addPermission)
      }
  }

  private val findUserPermissions: HttpRoutes[F] = findUserPermissionsEndpoint.toAuthHttpRoutes {
    case (id, token) =>
      authorize(token, Permissions.USER_MANAGEMENT_GET_USER_PERMISSIONS) { _ =>
        service.getPermissions(id)
      }
  }

  private val findPermissions: HttpRoutes[F] = findPermissionsEndpoint.toAuthHttpRoutes {
    case (size, page, token) =>
      authorize(token, Permissions.USER_MANAGEMENT_GET_PERMISSIONS) { _ =>
        validate(Pagination(size, page))
          .flatMap(service.getPermissions)
      }
  }
}

object PermissionApi {

  def apply[F[_]: Sync: ContextShift](
      permissionService: PermissionService[F],
      authService: AuthService[F]
  ): PermissionApi[F] = {
    new PermissionApi[F](permissionService, authService)
  }
}
