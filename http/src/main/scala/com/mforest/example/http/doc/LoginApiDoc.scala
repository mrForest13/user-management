package com.mforest.example.http.doc

import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir.model.UsernamePassword

trait LoginApiDoc extends Doc {

  type LoginResult = Endpoint[UsernamePassword, Fail[Error], (BarerToken, Ok[String]), Nothing]

  val loginUserEndpoint: LoginResult = {
    endpoint.get
      .tag("Login")
      .summary("login user")
      .in("api" / "login")
      .in(auth.basic)
      .out(header[BarerToken]("Authorization"))
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
}
