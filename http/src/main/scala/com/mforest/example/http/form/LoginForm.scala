package com.mforest.example.http.form

import cats.Functor.ops.toAllFunctorOps
import cats.implicits.catsKernelStdAlgebraForUnit
import com.mforest.example.core.validation.{Validator, validate}
import com.mforest.example.service.model.Credentials
import sttp.tapir.model.UsernamePassword

final case class LoginForm(credentials: UsernamePassword) {

  def toDto: Credentials = {
    Credentials(credentials.username, credentials.password.get)
  }
}

object LoginForm {

  def apply(username: String, password: Option[String]): LoginForm = {
    new LoginForm(UsernamePassword(username, password))
  }

  implicit val validator: Validator[LoginForm] = { form =>
    validate(form.credentials.password.forall(_.isEmpty), msg = "Password cannot be empty!")
      .combine(validate(form.credentials.username.isEmpty, msg = "Username cannot be empty!"))
      .as(form)
  }
}
