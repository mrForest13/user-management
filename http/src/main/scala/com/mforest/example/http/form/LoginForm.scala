package com.mforest.example.http.form

import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator
import com.mforest.example.service.model.Credentials
import sttp.tapir.model.UsernamePassword

final case class LoginForm(credentials: UsernamePassword) {

  def toDto: Credentials = {
    Credentials(credentials.username, credentials.password.get)
  }
}

object LoginForm {

  implicit val validator: Validator[LoginForm] = {
    case form: LoginForm if form.credentials.password.isEmpty =>
      ValidationError(s"Password cannot be empty!").invalid
    case form: LoginForm if form.credentials.password.get.isEmpty =>
      ValidationError(s"Password cannot be empty!").invalid
    case form @ (_: LoginForm) => form.valid
  }
}
