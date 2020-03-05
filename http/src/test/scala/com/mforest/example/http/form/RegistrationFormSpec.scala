package com.mforest.example.http.form

import cats.effect.IO
import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.validate
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Random

final class RegistrationFormSpec extends AnyWordSpec with Matchers {

  private val random = new Random

  "RegistrationForm" when {

    "call validate" must {

      "respond with user for valid data" in {
        val formMock = RegistrationFormSpec.formMock

        validate(formMock) shouldBe formMock.valid
      }

      "respond with validation error for incorrect email" in {
        val formMock = RegistrationFormSpec.formMock.copy(email = "incorrect")

        validate(formMock) shouldBe ValidationError("Invalid email address!").invalidNel
      }

      "respond with validation error for to short password" in {
        val formMock = RegistrationFormSpec.formMock.copy(password = "pass")

        validate(formMock) shouldBe ValidationError("Password should have minimum 8 characters!").invalidNel
      }

      "respond with validation error for to long password" in {
        val length   = 51
        val password = random.nextString(length)
        val formMock = RegistrationFormSpec.formMock.copy(password = password)

        validate(formMock) shouldBe ValidationError("Password cannot have more than 50 characters!").invalidNel
      }

      "respond with validation error for empty first name" in {
        val formMock = RegistrationFormSpec.formMock.copy(firstName = "")

        validate(formMock) shouldBe ValidationError("First name cannot be empty!").invalidNel
      }

      "respond with validation error for invalid first name" in {
        val formMock = RegistrationFormSpec.formMock.copy(firstName = "john1")

        validate(formMock) shouldBe ValidationError("First name can only contain letters!").invalidNel
      }

      "respond with validation error for to long first name" in {
        val length    = 100
        val firstName = random.nextString(length).filter(_.isLetter)
        val formMock  = RegistrationFormSpec.formMock.copy(firstName = firstName)

        validate(formMock) shouldBe ValidationError("First name cannot have more than 50 characters!").invalidNel
      }

      "respond with validation error for empty last name" in {
        val formMock = RegistrationFormSpec.formMock.copy(lastName = "")

        validate(formMock) shouldBe ValidationError("Last name cannot be empty!").invalidNel
      }

      "respond with validation error for invalid last name" in {
        val formMock = RegistrationFormSpec.formMock.copy(lastName = "john1")

        validate(formMock) shouldBe ValidationError("Last name can only contain letters!").invalidNel
      }

      "respond with validation error for to long last name" in {
        val length   = 100
        val lastName = random.nextString(length).filter(_.isLetter)
        val formMock = RegistrationFormSpec.formMock.copy(lastName = lastName)

        validate(formMock) shouldBe ValidationError("Last name cannot have more than 50 characters!").invalidNel
      }

      "respond with validation error for empty city" in {
        val formMock = RegistrationFormSpec.formMock.copy(city = "")

        validate(formMock) shouldBe ValidationError("City cannot be empty!").invalidNel
      }

      "respond with validation error for invalid city" in {
        val formMock = RegistrationFormSpec.formMock.copy(city = "john1")

        validate(formMock) shouldBe ValidationError("City can only contain letters!").invalidNel
      }

      "respond with validation error for to long city" in {
        val length   = 100
        val city     = random.nextString(length).filter(_.isLetter)
        val formMock = RegistrationFormSpec.formMock.copy(city = city)

        validate(formMock) shouldBe ValidationError("City cannot have more than 50 characters!").invalidNel
      }

      "respond with validation error for empty country" in {
        val formMock = RegistrationFormSpec.formMock.copy(country = "")

        validate(formMock) shouldBe ValidationError("Country cannot be empty!").invalidNel
      }

      "respond with validation error for invalid country" in {
        val formMock = RegistrationFormSpec.formMock.copy(country = "john1")

        validate(formMock) shouldBe ValidationError("Country can only contain letters!").invalidNel
      }

      "respond with validation error for to long country" in {
        val length   = 100
        val country  = random.nextString(length).filter(_.isLetter)
        val formMock = RegistrationFormSpec.formMock.copy(country = country)

        validate(formMock) shouldBe ValidationError("Country cannot have more than 50 characters!").invalidNel
      }

      "respond with validation error for empty phone number" in {
        val formMock = RegistrationFormSpec.formMock.copy(phone = "")

        validate(formMock) shouldBe ValidationError("Phone number cannot be empty!").invalidNel
      }

      "respond with validation error for invalid phone number" in {
        val formMock = RegistrationFormSpec.formMock.copy(phone = "john1")

        validate(formMock) shouldBe ValidationError("Phone number can only contain numbers!").invalidNel
      }

      "respond with validation error for to long phone number" in {
        val phoneNumber = "1111155556666722"
        val formMock    = RegistrationFormSpec.formMock.copy(phone = phoneNumber)

        validate(formMock) shouldBe ValidationError("Phone number cannot be longer than 15 digits!").invalidNel
      }
    }
  }
}

object RegistrationFormSpec {

  val formMock: RegistrationForm = RegistrationForm(
    email = "john.smith@gmail.com",
    password = "examplea",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )

  implicit val encoder: EntityEncoder[IO, RegistrationForm] =
    jsonEncoderOf[IO, RegistrationForm]
}
