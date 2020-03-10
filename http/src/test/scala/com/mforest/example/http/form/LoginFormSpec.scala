package com.mforest.example.http.form

import cats.data.NonEmptyList
import cats.implicits.{catsSyntaxOptionId, catsSyntaxValidatedId, none}
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.validate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class LoginFormSpec extends AnyWordSpec with Matchers {

  "RegistrationForm" when {

    "call validate" must {

      "respond with credentials for valid data" in {
        val formMock = LoginFormSpec.formMock

        validate(formMock) shouldBe formMock.valid
      }

      "respond with validation error for empty username" in {
        val formMock = LoginForm("", "password".some)

        validate(formMock) shouldBe ValidationError("Username cannot be empty!").invalidNel
      }

      "respond with validation error for empty password" in {
        val formMock = LoginForm("username", "".some)

        validate(formMock) shouldBe ValidationError("Password cannot be empty!").invalidNel
      }

      "respond with validation error for no password" in {
        val formMock = LoginForm("username", none)

        validate(formMock) shouldBe ValidationError("Password cannot be empty!").invalidNel
      }

      "respond with validation error for empty password and username" in {
        val formMock = LoginForm("", "".some)

        val usernameError = ValidationError("Password cannot be empty!")
        val passwordError = ValidationError("Username cannot be empty!")

        validate(formMock) shouldBe NonEmptyList.of(usernameError, passwordError).invalid
      }
    }
  }
}

object LoginFormSpec {

  val formMock: LoginForm = LoginForm("username", "password".some)
}
