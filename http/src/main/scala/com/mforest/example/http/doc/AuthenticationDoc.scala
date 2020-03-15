package com.mforest.example.http.doc

import cats.data.NonEmptyList
import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BearerToken
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir.model.UsernamePassword

private[http] trait AuthenticationDoc extends Doc {

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(loginUserEndpoint, logoutUserEndpoint)

  protected val loginUserEndpoint: Endpoint[UsernamePassword, Fail[Error], (BearerToken, Ok[String]), Nothing] = {
    endpoint.post
      .tag("Authentication Api")
      .summary("create token for user")
      .in("api" / "login")
      .in(auth.basic)
      .out(header[BearerToken]("Authorization"))
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Ok,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok(s"Login succeeded")
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
            StatusCode.Unauthorized,
            jsonBody[Fail[Error.UnauthorizedError]]
              .example(
                StatusResponse.Fail(Error.UnauthorizedError("Wrong email or password!"))
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

  protected val logoutUserEndpoint: Endpoint[String, Fail[Error], Ok[String], Nothing] = {
    endpoint.delete
      .tag("Authentication Api")
      .summary("delete user token")
      .in("api" / "logout")
      .in(auth.bearer)
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Ok,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok(s"Logout succeeded")
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
