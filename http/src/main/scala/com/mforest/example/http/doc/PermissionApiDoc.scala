package com.mforest.example.http.doc

import java.util.UUID

import cats.data.Chain
import cats.syntax.option._
import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.form.AddPermissionForm
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.dto.PermissionDto
import io.chrisdavenport.fuuid.FUUID
import sttp.model.StatusCode
import sttp.tapir.Endpoint

trait PermissionApiDoc extends Doc {

  override def endpoints: Seq[Endpoint[_, _, _, _]] = {
    Seq(addPermissionEndpoint, deletePermissionEndpoint, findPermissionsEndpoint)
  }

  protected val addPermissionEndpoint: Endpoint[AddPermissionForm, Fail[Error], Ok[String], Nothing] = {
    endpoint.post
      .tag("Permission Api")
      .summary("Add new permission")
      .in("permissions")
      .in(
        jsonBody[AddPermissionForm]
          .example(PermissionApiDoc.form)
      )
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

  protected val deletePermissionEndpoint: Endpoint[FUUID, Fail[Error], Ok[String], Nothing] = {
    endpoint.delete
      .tag("Permission Api")
      .summary("Delete permission")
      .in("permissions" / path[FUUID]("permissionId"))
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Created,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok("The permission with name EXAMPLE_PERMISSION has been deleted")
              )
          )
        )
      )
      .errorOut(
        oneOf[Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.NotFound,
            jsonBody[Fail[Error.NotFoundError]]
              .example(
                StatusResponse.Fail(Error.NotFoundError("The permission with name EXAMPLE_PERMISSION not exists!"))
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

  protected val findPermissionsEndpoint: Endpoint[PaginationParams, Fail[Error], Ok[Chain[PermissionDto]], Nothing] = {
    endpoint.get
      .tag("Permission Api")
      .summary("Find permissions")
      .in("permissions")
      .in(query[Option[Int]]("size").example(10.some))
      .in(query[Option[Int]]("page").example(0.some))
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
    id = FUUID.fromUUID(UUID.randomUUID()),
    name = "FIRST_EXAMPLE_PERMISSION"
  )

  private val form = AddPermissionForm("EXAMPLE_PERMISSION")
}
