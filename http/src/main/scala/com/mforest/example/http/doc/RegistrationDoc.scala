package com.mforest.example.http.doc

import cats.data.NonEmptyList
import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.form.RegistrationForm
import com.mforest.example.http.response.StatusResponse
import sttp.model.StatusCode
import sttp.tapir.Endpoint

private[http] trait RegistrationDoc extends Doc {

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(registerUserEndpoint)

  protected val registerUserEndpoint: Endpoint[RegistrationForm, Fail[Error], Ok[String], Nothing] = {
    endpoint.post
      .tag("Registration Api")
      .summary("Create user")
      .in("users")
      .in(
        jsonBody[RegistrationForm]
          .example(RegistrationDoc.form)
      )
      .out(
        oneOf(
          statusMappingFromMatchType(
            StatusCode.Created,
            jsonBody[Ok[String]]
              .example(
                StatusResponse.Ok("The user with email john.smith@gmail.com has been created")
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
                StatusResponse.Fail(Error.ConflictError("The user with email example@gmail.com already exists!"))
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
                StatusResponse.Fail(Error.ValidationError("Email cannot be empty!"))
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

object RegistrationDoc {

  private val form = RegistrationForm(
    email = "john.smith@gmail.com",
    password = "example",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )
}
