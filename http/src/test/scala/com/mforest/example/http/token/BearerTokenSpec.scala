package com.mforest.example.http.token

import java.time.Instant

import cats.implicits.{catsSyntaxOptionId, toShow}
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class BearerTokenSpec extends AnyWordSpec with Matchers {

  "BarerToken" when {

    "show" must {

      "respond with added barer prefix for string token" in {
        val token      = "example"
        val barerToken = BearerToken.fromString(token)

        barerToken.show shouldBe s"Bearer $token"
      }

      "respond with added barer prefix for random id token" in {
        val token      = SecureRandomId.Strong.generate
        val barerToken = new BearerToken(token)

        barerToken.show shouldBe s"Bearer $token"
      }

      "respond with added barer prefix for tsec  token" in {
        val identity = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val token = TSecBearerToken(
          id = SecureRandomId.Strong.generate,
          identity = identity,
          expiry = Instant.now,
          lastTouched = Instant.now.some
        )
        val barerToken = BearerToken(token)

        barerToken.show shouldBe s"Bearer ${token.id}"
      }
    }
  }
}
