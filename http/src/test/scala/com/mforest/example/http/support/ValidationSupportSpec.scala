package com.mforest.example.http.support

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.http.HttpSpec
import com.mforest.example.http.form.AddPermissionForm
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class ValidationSupportSpec extends AsyncWordSpec with HttpSpec with ValidationSupport with Matchers {

  "ValidationSupport" when {

    "call validate" must {

      "respond with valid data" in {
        val data = AddPermissionForm("EXAMPLE_PERMISSION")

        val result = validate[IO, AddPermissionForm](data)

        result.value.asserting(_ shouldBe data.asRight)
      }

      "respond with validation errors" in {
        val data = AddPermissionForm("")

        val patternError = "Wrong permission form. It should look like XXX_XXX!"
        val nameError    = "Name cannot be empty!"

        val error = ValidationError(patternError concat System.lineSeparator() concat nameError)

        val result = validate[IO, AddPermissionForm](data)

        result.value.asserting(_ shouldBe error.asLeft)
      }
    }
  }
}
