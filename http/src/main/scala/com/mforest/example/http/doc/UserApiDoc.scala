package com.mforest.example.http.doc

import cats.data.{Chain, NonEmptyList}
import cats.effect.IO
import com.mforest.example.core.error.Error
import com.mforest.example.core.permission.Permissions
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.dto.{PermissionDto, UserDto}
import io.chrisdavenport.fuuid.FUUID
import sttp.model.StatusCode
import sttp.tapir.Endpoint

private[http] trait UserApiDoc extends Doc {

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = {
    NonEmptyList.of(addPermissionEndpoint, revokePermissionEndpoint, findUsersEndpoint)
  }

  protected val addPermissionEndpoint
      : Endpoint[(FUUID, FUUID, Token), Fail[Error], (BearerToken, Ok[String]), Nothing] = {
    endpoint.post
      .tag("User Api")
      .summary("Add permission for user")
      .description(s"Permission ${Permissions.USER_MANAGEMENT_ADD_PERMISSION_FOR_USERS}")
      .in("users" / path[FUUID]("userId") / "permissions" / path[FUUID]("permissionId"))
      .in(auth.bearer)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Ok,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok("The permission has been added!")
              )
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.BadRequest,
            jsonBody[Fail[Error.ValidationError]]
              .example(
                StatusResponse.Fail(Error.ValidationError("Invalid value for: header Authorization!"))
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
            StatusCode.NotFound,
            jsonBody[Fail[Error.ConflictError]]
              .example(
                StatusResponse.Fail(Error.ConflictError("The User or permission does not exist!"))
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
            StatusCode.InternalServerError,
            jsonBody[Fail[Error.InternalError]]
              .example(
                StatusResponse.Fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }

  protected val revokePermissionEndpoint
      : Endpoint[(FUUID, FUUID, Token), Fail[Error], (BearerToken, Ok[String]), Nothing] = {
    endpoint.delete
      .tag("User Api")
      .summary("Revoke permission for user")
      .description(s"Permission ${Permissions.USER_MANAGEMENT_REVOKE_PERMISSION_FOR_USERS}")
      .in("users" / path[FUUID]("userId") / "permissions" / path[FUUID]("permissionId"))
      .in(auth.bearer)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Ok,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok("The permission has been revoked!")
              )
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.BadRequest,
            jsonBody[Fail[Error.ValidationError]]
              .example(
                StatusResponse.Fail(Error.ValidationError("Invalid value for: header Authorization!"))
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
            StatusCode.NotFound,
            jsonBody[Fail[Error.NotFoundError]]
              .example(
                StatusResponse.Fail(Error.NotFoundError("The User or permission does not exist!"))
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
            StatusCode.InternalServerError,
            jsonBody[Fail[Error.InternalError]]
              .example(
                StatusResponse.Fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }

  protected val findUsersEndpoint
      : Endpoint[PaginationParams, Fail[Error], (BearerToken, Ok[Chain[UserDto]]), Nothing] = {
    endpoint.get
      .tag("User Api")
      .summary("Find users")
      .description(s"Permission ${Permissions.USER_MANAGEMENT_GET_USERS}")
      .in("users")
      .in(query[Option[Int]]("size").example(10.some))
      .in(query[Option[Int]]("page").example(0.some))
      .in(auth.bearer)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingClassMatcher(
            StatusCode.Ok,
            jsonBody[Ok[Chain[UserDto]]]
              .example(
                StatusResponse.Ok(
                  Chain(UserApiDoc.dto, UserApiDoc.dto, UserApiDoc.dto)
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

object UserApiDoc {

  private def dto = UserDto(
    id = FUUID.randomFUUID[IO].unsafeRunSync(),
    email = "john.smith@gmail.com",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )
}
