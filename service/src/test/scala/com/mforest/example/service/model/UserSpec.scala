package com.mforest.example.service.model

import cats.implicits._
import com.mforest.example.core.validation._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserSpec extends AnyWordSpec with Matchers {

  "User" when {

    "call validate" must {

      "respond with user for valid data" in {

        validate(UserSpec.userMock) shouldBe UserSpec.userMock.valid
      }
    }
  }
}

object UserSpec {

  private val userMock = User(
    email = "john.smith@gmail.com",
    password = "example",
    firstName = "john",
    lastName = "smith",
    city = "London",
    country = "England",
    phone = "123456789"
  )
}
