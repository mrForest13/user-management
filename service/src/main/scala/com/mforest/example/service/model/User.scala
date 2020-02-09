package com.mforest.example.service.model

import cats.implicits._
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class User(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
)

object User {

  implicit val validator: Validator[User] = {
    case user: User if user.email.isEmpty =>
      ValidationError(s"Email cannot be empty!").invalid
    case user: User if user.password.isEmpty =>
      ValidationError(s"Password cannot be empty!").invalid
    case user: User if user.firstName.isEmpty =>
      ValidationError(s"First name cannot be empty!").invalid
    case user: User if user.lastName.isEmpty =>
      ValidationError(s"Last name cannot be empty!").invalid
    case user: User if user.city.isEmpty =>
      ValidationError(s"City cannot be empty!").invalid
    case user: User if user.country.isEmpty =>
      ValidationError(s"Country cannot be empty!").invalid
    case user: User if user.phone.isEmpty =>
      ValidationError(s"Phone cannot be empty!").invalid
    case user @ (_: User) => user.valid
  }

  implicit val encoder: Encoder[User] = deriveEncoder
  implicit val decoder: Decoder[User] = deriveDecoder
}
