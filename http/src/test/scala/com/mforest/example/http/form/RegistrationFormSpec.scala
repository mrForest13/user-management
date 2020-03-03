package com.mforest.example.http.form

import cats.effect.IO
import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.validation.validate
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RegistrationFormSpec extends AnyWordSpec with Matchers {

  "RegistrationForm" when {

    "call validate" must {

      "respond with user for valid data" in {
        val formMock = RegistrationFormSpec.formMock

        validate(formMock) shouldBe formMock.valid
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
