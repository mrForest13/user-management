package com.mforest.example.http.doc

import java.util.UUID

import cats.data.Chain
import cats.syntax.option._
import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.dto.{PermissionDto, UserDto}
import io.chrisdavenport.fuuid.FUUID
import sttp.model.StatusCode
import sttp.tapir.Endpoint

trait UserApiDoc extends Doc {

  override def endpoints: Seq[Endpoint[_, _, _, _]] = {
    Seq(addPermissionEndpoint, revokePermissionEndpoint, findUsersEndpoint)
  }

  protected val addPermissionEndpoint: Endpoint[(FUUID, FUUID), Fail[Error], Ok[String], Nothing] = {
    endpoint.post
      .tag("User")
      .summary("Add permission for user")
      .in("users" / path[FUUID]("userId") / "permissions" / path[FUUID]("permissionId"))
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

  protected val revokePermissionEndpoint: Endpoint[(FUUID, FUUID), Fail[Error], Ok[String], Nothing] = {
    endpoint.delete
      .tag("User")
      .summary("Revoke permission for user")
      .in("users" / path[FUUID]("userId") / "permissions" / path[FUUID]("permissionId"))
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

  protected val findUsersEndpoint: Endpoint[PaginationParams, Fail[Error], Ok[Chain[UserDto]], Nothing] = {
    endpoint.get
      .tag("User")
      .summary("Find users")
      .in("users")
      .in(query[Option[Int]]("size").example(10.some))
      .in(query[Option[Int]]("page").example(0.some))
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
    id = FUUID.fromUUID(UUID.randomUUID()),
    email = "john.smith@gmail.com",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )
}
