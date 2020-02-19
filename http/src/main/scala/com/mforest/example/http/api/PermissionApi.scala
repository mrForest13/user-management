package com.mforest.example.http.api

import cats.SemigroupK.nonInheritedOps._
import cats.effect.{ContextShift, Sync}
import com.mforest.example.core.model.Pagination
import com.mforest.example.http.Api
import com.mforest.example.http.doc.PermissionApiDoc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.permission.PermissionService
import org.http4s.HttpRoutes

class PermissionApi[F[_]: Sync: ContextShift](permissionService: PermissionService[F])
    extends Api[F]
    with PermissionApiDoc {

  override def routes: HttpRoutes[F] = addPermission <+> deletePermission <+> findPermissions

  private val addPermission: HttpRoutes[F] = addPermissionEndpoint.toHandleRoutes { request =>
    validate(request)
      .map(_.toDto)
      .flatMap(permissionService.addPermission)
      .bimap(StatusResponse.fail, StatusResponse.ok)
  }

  private val deletePermission: HttpRoutes[F] = deletePermissionEndpoint.toHandleRoutes { id =>
    permissionService
      .deletePermission(id)
      .bimap(StatusResponse.fail, StatusResponse.ok)
  }

  private val findPermissions: HttpRoutes[F] = findPermissionsEndpoint.toHandleRoutes { pagination =>
    validate(Pagination(pagination))
      .flatMap(permissionService.getPermissions)
      .bimap(StatusResponse.fail, StatusResponse.ok)
  }
}

object PermissionApi {

  def apply[F[_]: Sync: ContextShift](loginService: PermissionService[F]): PermissionApi[F] = {
    new PermissionApi(loginService)
  }
}
