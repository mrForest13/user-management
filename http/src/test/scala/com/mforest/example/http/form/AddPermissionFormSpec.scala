package com.mforest.example.http.form

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.validate
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Random

final class AddPermissionFormSpec extends AnyWordSpec with Matchers {

  "AddPermissionForm" when {

    "call validate" must {

      "respond with permission for valid data" in {
        val formMock = AddPermissionFormSpec.formMock

        validate(formMock) shouldBe formMock.valid
      }

      "respond with validation error for wrong format permission name" in {
        val formMock = AddPermissionForm("example_permission")

        val validationError = "Wrong permission form. It should look like XXX_XXX!"

        validate(formMock) shouldBe ValidationError(validationError).invalidNel
      }

      "respond with validation error for empty permission name" in {
        val formMock = AddPermissionForm("")

        val patternError = ValidationError("Wrong permission form. It should look like XXX_XXX!")
        val nameError    = ValidationError("Name cannot be empty!")

        validate(formMock) shouldBe NonEmptyList.of(patternError, nameError).invalid
      }

      "respond with validation error for to long permission name" in {
        val length   = 101
        val name     = Random.nextString(length)
        val formMock = AddPermissionForm(name)

        val patternError = ValidationError("Wrong permission form. It should look like XXX_XXX!")
        val nameError    = ValidationError("Name cannot be longer than 100 characters!")

        validate(formMock) shouldBe NonEmptyList.of(patternError, nameError).invalid
      }
    }
  }
}

object AddPermissionFormSpec {

  val formMock: AddPermissionForm = AddPermissionForm("EXAMPLE_PERMISSION")

  implicit val encoder: EntityEncoder[IO, AddPermissionForm] =
    jsonEncoderOf[IO, AddPermissionForm]
}
