package com.mforest.example.http.doc

import cats.data.{Chain, NonEmptyList}
import cats.effect.IO
import com.mforest.example.core.error.Error
import com.mforest.example.core.permission.Permissions
import com.mforest.example.http.Doc
import com.mforest.example.http.form.AddPermissionForm
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.dto.PermissionDto
import io.chrisdavenport.fuuid.FUUID
import sttp.model.StatusCode
import sttp.tapir.Endpoint

private[http] trait PermissionApiDoc extends Doc {

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = {
    NonEmptyList.of(addPermissionEndpoint, findUserPermissionsEndpoint, findPermissionsEndpoint)
  }

  protected val addPermissionEndpoint
      : Endpoint[(Token, AddPermissionForm), Fail[Error], (BearerToken, Ok[String]), Nothing] = {
    endpoint.post
      .tag("Permission Api")
      .summary("Add new permission")
      .description(s"Permission ${Permissions.USER_MANAGEMENT_ADD_PERMISSION}")
      .in("permissions")
      .in(auth.bearer)
      .in(
        jsonBody[AddPermissionForm]
          .example(PermissionApiDoc.form)
      )
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Created,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok("The permission with name EXAMPLE_PERMISSION has been created")
              )
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.Conflict,
            jsonBody[Fail[Error.ConflictError]]
              .example(
                StatusResponse.Fail(Error.ConflictError("The permission with name EXAMPLE_PERMISSION already exists!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.Forbidden,
            jsonBody[Fail[Error.ForbiddenError]]
              .example(
                StatusResponse.Fail(
                  Error.ForbiddenError("The server is refusing to respond to it! You don't have permission!")
                )
              )
          ),
          statusMappingFromMatchType(
            StatusCode.ServiceUnavailable,
            jsonBody[Fail[Error.UnavailableError]]
              .example(
                StatusResponse.Fail(Error.UnavailableError("The server is currently unavailable!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.BadRequest,
            jsonBody[Fail[Error.ValidationError]]
              .example(
                StatusResponse.Fail(Error.ValidationError("Permission cannot be empty!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.InternalServerError,
            jsonBody[Fail[Error.InternalError]]
              .example(
                StatusResponse.Fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }

  protected val findUserPermissionsEndpoint
      : Endpoint[(FUUID, Token), Fail[Error], (BearerToken, Ok[Chain[PermissionDto]]), Nothing] = {
    endpoint.get
      .tag("Permission Api")
      .summary("Find user permissions")
      .description(s"Permission ${Permissions.USER_MANAGEMENT_GET_USER_PERMISSIONS}")
      .in("users" / path[FUUID]("userId") / "permissions")
      .in(auth.bearer)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingClassMatcher(
            StatusCode.Ok,
            jsonBody[Ok[Chain[PermissionDto]]]
              .example(
                StatusResponse.Ok(
                  Chain(PermissionApiDoc.dto, PermissionApiDoc.dto, PermissionApiDoc.dto)
                )
              ),
            classOf[Ok[Chain[PermissionDto]]]
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.Forbidden,
            jsonBody[Fail[Error.ForbiddenError]]
              .example(
                StatusResponse.Fail(
                  Error.ForbiddenError("The server is refusing to respond to it! You don't have permission!")
                )
              )
          ),
          statusMappingFromMatchType(
            StatusCode.ServiceUnavailable,
            jsonBody[Fail[Error.UnavailableError]]
              .example(
                StatusResponse.Fail(Error.UnavailableError("The server is currently unavailable!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.BadRequest,
            jsonBody[Fail[Error.ValidationError]]
              .example(
                StatusResponse.Fail(Error.ValidationError("Invalid value for: header Authorization!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.InternalServerError,
            jsonBody[Fail[Error.InternalError]]
              .example(
                StatusResponse.Fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }

  protected val findPermissionsEndpoint
      : Endpoint[PaginationParams, Fail[Error], (BearerToken, Ok[Chain[PermissionDto]]), Nothing] = {
    endpoint.get
      .tag("Permission Api")
      .summary("Find permissions")
      .description(s"Permission ${Permissions.USER_MANAGEMENT_GET_PERMISSIONS}")
      .in("permissions")
      .in(query[Option[Int]]("size").example(10.some))
      .in(query[Option[Int]]("page").example(0.some))
      .in(auth.bearer)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingClassMatcher(
            StatusCode.Ok,
            jsonBody[Ok[Chain[PermissionDto]]]
              .example(
                StatusResponse.Ok(
                  Chain(PermissionApiDoc.dto, PermissionApiDoc.dto, PermissionApiDoc.dto)
                )
              ),
            classOf[Ok[Chain[PermissionDto]]]
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.Forbidden,
            jsonBody[Fail[Error.ForbiddenError]]
              .example(
                StatusResponse.Fail(
                  Error.ForbiddenError("The server is refusing to respond to it! You don't have permission!")
                )
              )
          ),
          statusMappingFromMatchType(
            StatusCode.ServiceUnavailable,
            jsonBody[Fail[Error.UnavailableError]]
              .example(
                StatusResponse.Fail(Error.UnavailableError("The server is currently unavailable!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.BadRequest,
            jsonBody[Fail[Error.ValidationError]]
              .example(
                StatusResponse.Fail(Error.ValidationError("Size cannot be less than 0!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.InternalServerError,
            jsonBody[Fail[Error.InternalError]]
              .example(
                StatusResponse.Fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }
}

object PermissionApiDoc {

  private def dto = PermissionDto(
    id = FUUID.randomFUUID[IO].unsafeRunSync(),
    name = "FIRST_EXAMPLE_PERMISSION"
  )

  private val form = AddPermissionForm("EXAMPLE_PERMISSION")
}
