package com.mforest.example.service.model

import cats.implicits._
import com.mforest.example.core.validation._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RegistrationFormSpec extends AnyWordSpec with Matchers {

  "RegistrationForm" when {

    "call validate" must {

      "respond with user for valid data" in {

        validate(RegistrationFormSpec.userMock) shouldBe RegistrationFormSpec.userMock.valid
      }
    }
  }
}

object RegistrationFormSpec {

  private val userMock = RegistrationForm(
    email = "john.smith@gmail.com",
    password = "example",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )
}
