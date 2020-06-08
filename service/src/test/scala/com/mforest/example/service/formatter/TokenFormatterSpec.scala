package com.mforest.example.service.formatter

import java.time.Instant

import cats.Id
import cats.implicits.{catsSyntaxEitherId, none}
import io.chrisdavenport.fuuid.FUUID
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class TokenFormatterSpec extends AnyWordSpec with Matchers with TokenFormatter {

  "TokenFormatter" when {

    "call as json" must {

      "respond with token json string" in {
        val expiry = Instant.parse("2014-12-03T10:15:30.00Z");
        val fuuid  = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val token = TSecBearerToken[Id[FUUID]](
          id = SecureRandomId("id"),
          identity = fuuid,
          expiry = expiry,
          lastTouched = none
        )

        token.asJson.noSpaces shouldBe
          """{"id":"id","identity":"8ea16e29-3978-4113-8a06-eca8228f78ff",
            |"expiry":1417601730000,"lastTouched":null}""".stripMargin
            .replaceAll(System.lineSeparator, "")
      }
    }

    "call decode" must {

      "respond with token from string" in {
        val expiry = Instant.parse("2014-12-03T10:15:30.00Z");
        val fuuid  = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val json =
          """{"id":"id","identity":"8ea16e29-3978-4113-8a06-eca8228f78ff",
            |"expiry":1417601730000,"lastTouched":null}""".stripMargin
            .replaceAll(System.lineSeparator, "")

        decode[TSecBearerToken[Id[FUUID]]](json) shouldBe TSecBearerToken[Id[FUUID]](
          id = SecureRandomId("id"),
          identity = fuuid,
          expiry = expiry,
          lastTouched = none
        ).asRight
      }
    }
  }
}
