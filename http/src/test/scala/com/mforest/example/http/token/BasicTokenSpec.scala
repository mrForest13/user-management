package com.mforest.example.http.token

import cats.implicits.toShow
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class BasicTokenSpec  extends AnyWordSpec with Matchers {

  "BasicToken" when {

    "show" must {

      "respond with added basic prefix for string token" in {
        val token = "example"
        val barerToken =  BasicToken(token)

        barerToken.show shouldBe s"Basic $token"
      }
    }
  }
}
