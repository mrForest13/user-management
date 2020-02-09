package com.mforest.example.http.doc

import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir.model.UsernamePassword

trait LoginApiDoc extends Doc {

  type LoginResult = Endpoint[UsernamePassword, StatusResponse.Fail[Error], StatusResponse.Ok[String], Nothing]

  val loginUserEndpoint: LoginResult = {
    endpoint.get
      .tag("Login")
      .summary("login user")
      .in("api" / "login")
      .in(auth.basic)
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Created,
            jsonBody[StatusResponse.Ok[String]]
              .example(
                StatusResponse.ok(s"Login succeeded")
              )
          )
        )
      )
      .errorOut(
        oneOf[StatusResponse.Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.Unauthorized,
            jsonBody[StatusResponse.Fail[Error.UnauthorizedError]]
              .example(
                StatusResponse.fail(Error.UnauthorizedError("Wrong email or password!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.ServiceUnavailable,
            jsonBody[StatusResponse.Fail[Error.UnavailableError]]
              .example(
                StatusResponse.fail(Error.UnavailableError("The server is currently unavailable!"))
              )
          ),
          statusMappingFromMatchType(
            StatusCode.InternalServerError,
            jsonBody[StatusResponse.Fail[Error.InternalError]]
              .example(
                StatusResponse.fail(Error.InternalError("There was an internal server error!"))
              )
          )
        )
      )
  }
}
