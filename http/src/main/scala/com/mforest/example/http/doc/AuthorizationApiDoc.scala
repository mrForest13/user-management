package com.mforest.example.http.doc

import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import sttp.model.StatusCode
import sttp.tapir.Endpoint

private[http] trait AuthorizationApiDoc extends Doc {

  override def endpoints: Seq[Endpoint[_, _, _, _]] = Seq(validatePermissionEndpoint)

  protected val validatePermissionEndpoint: Endpoint[(Token, Token), Fail[Error], (BarerToken, Ok[String]), Nothing] = {
    endpoint.get
      .tag("Authorization Api")
      .summary("Valid user permission")
      .in("permissions" / path[String]("permission") / "validate")
      .in(auth.bearer)
      .out(header[BarerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Ok,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok("The user has the required permission!")
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
            StatusCode.NotFound,
            jsonBody[Fail[Error.NotFoundError]]
              .example(
                StatusResponse.Fail(Error.NotFoundError("The permission with name EXAMPLE_PERMISSION not exists!"))
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
