package com.mforest.example.service.form

import cats.implicits._
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class RegistrationForm(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
)

object RegistrationForm {

  implicit val validator: Validator[RegistrationForm] = {
    case form: RegistrationForm if form.email.isEmpty =>
      ValidationError(s"Email cannot be empty!").invalid
    case form: RegistrationForm if form.password.isEmpty =>
      ValidationError(s"Password cannot be empty!").invalid
    case form: RegistrationForm if form.firstName.isEmpty =>
      ValidationError(s"First name cannot be empty!").invalid
    case form: RegistrationForm if form.lastName.isEmpty =>
      ValidationError(s"Last name cannot be empty!").invalid
    case form: RegistrationForm if form.city.isEmpty =>
      ValidationError(s"City cannot be empty!").invalid
    case form: RegistrationForm if form.country.isEmpty =>
      ValidationError(s"Country cannot be empty!").invalid
    case form: RegistrationForm if form.phone.isEmpty =>
      ValidationError(s"Phone cannot be empty!").invalid
    case form @ (_: RegistrationForm) => form.valid
  }

  implicit val encoder: Encoder[RegistrationForm] = deriveEncoder
  implicit val decoder: Decoder[RegistrationForm] = deriveDecoder
}
