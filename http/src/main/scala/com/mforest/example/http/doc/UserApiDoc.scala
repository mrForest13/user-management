package com.mforest.example.http.doc

import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.service.user.User
import sttp.model.StatusCode
import sttp.tapir.Endpoint

trait UserApiDoc extends Doc {

  def docs: Seq[Endpoint[_, _, _, _]] = Seq(addUserEndpoint)

  val addUserEndpoint: Endpoint[User, StatusResponse.Fail[Error], StatusResponse.Ok[String], Nothing] = {
    endpoint.post
      .tag("Users")
      .summary("Create user")
      .in("users")
      .in(
        jsonBody[User]
          .example(UserApiDoc.user)
      )
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Created,
            jsonBody[StatusResponse.Ok[String]]
              .example(
                StatusResponse.ok(s"The user with email john.smith@gmail.com has been created")
              )
          )
        )
      )
      .errorOut(
        oneOf[StatusResponse.Fail[Error]](
          statusMappingFromMatchType(
            StatusCode.Conflict,
            jsonBody[StatusResponse.Fail[Error.ConflictError]]
              .example(
                StatusResponse.fail(Error.ConflictError("User with email example@gmail.com already exists!"))
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
            StatusCode.BadRequest,
            jsonBody[StatusResponse.Fail[Error.ValidationError]]
              .example(
                StatusResponse.fail(Error.ValidationError("Email cannot be empty!"))
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

object UserApiDoc {

  private val user = User(
    email = "john.smith@gmail.com",
    password = "example",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )
}
