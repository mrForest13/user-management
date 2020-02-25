package com.mforest.example.http.form

import cats.Functor.ops.toAllFunctorOps
import cats.implicits.catsKernelStdAlgebraForUnit
import com.mforest.example.core.validation.{Validator, validate}
import com.mforest.example.service.model.User
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import scala.util.matching.Regex

final case class RegistrationForm(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
) {

  def toDto: User = {
    User(
      email = email,
      password = password,
      firstName = firstName,
      lastName = lastName,
      city = city,
      country = country,
      phone = phone
    )
  }
}

object RegistrationForm {

  private val emailRegex: Regex =
    """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0
       c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a
       -z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[
       0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x0
       8\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".stripMargin.r

  implicit val validator: Validator[RegistrationForm] = { form =>
    validate(emailRegex.matches(form.email), msg = "Invalid email address!")
      .combine(validate(form.password.length < 8, msg = "Password should have minimum 8 characters!"))
      .combine(validate(form.password.length > 50, msg = "Password cannot have more than 50 characters!"))
      .combine(validate(!form.firstName.isEmpty, msg = "First name cannot be empty!"))
      .combine(validate(!form.firstName.forall(_.isLetter), msg = "First name can only contain letters!"))
      .combine(validate(form.firstName.length > 50, msg = "First name cannot have more than 50 characters!"))
      .combine(validate(!form.lastName.isEmpty, msg = "Last name cannot be empty!"))
      .combine(validate(!form.lastName.forall(_.isLetter), msg = "Last name can only contain letters!"))
      .combine(validate(form.lastName.length > 50, msg = "Last name cannot have more than 50 characters!"))
      .combine(validate(!form.city.isEmpty, msg = "City cannot be empty!"))
      .combine(validate(!form.city.forall(_.isLetter), msg = "City can only contain letters!"))
      .combine(validate(form.city.length > 50, msg = "City cannot have more than 50 characters!"))
      .combine(validate(!form.country.isEmpty, msg = "Country cannot be empty!"))
      .combine(validate(!form.country.forall(_.isLetter), msg = "Country can only contain letters!"))
      .combine(validate(form.country.length > 50, msg = "Country cannot have more than 50 characters!"))
      .combine(validate(!form.phone.isEmpty, msg = "Phone cannot be empty!"))
      .combine(validate(!form.phone.forall(_.isDigit), msg = "Phone number can only contain numbers!"))
      .combine(validate(form.phone.length > 15, msg = "Phone number cannot be longer than 15 digits!"))
      .as(form)
  }

  implicit val encoder: Encoder[RegistrationForm] = deriveEncoder
  implicit val decoder: Decoder[RegistrationForm] = deriveDecoder
}
