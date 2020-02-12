package com.mforest.example.http.doc

import com.mforest.example.core.error.Error
import com.mforest.example.http.Doc
import com.mforest.example.http.form.RegistrationForm
import com.mforest.example.http.response.StatusResponse
import sttp.model.StatusCode
import sttp.tapir.Endpoint

trait RegistrationApiDoc extends Doc {

  type RegisterUser = Endpoint[RegistrationForm, StatusResponse.Fail[Error], StatusResponse.Ok[String], Nothing]

  val registerUserEndpoint: RegisterUser = {
    endpoint.post
      .tag("Registration")
      .summary("Create user")
      .in("users")
      .in(
        jsonBody[RegistrationForm]
          .example(RegistrationApiDoc.form)
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

object RegistrationApiDoc {

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
