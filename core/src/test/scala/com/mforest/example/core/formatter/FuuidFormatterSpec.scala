package com.mforest.example.core.formatter

import io.chrisdavenport.fuuid.FUUID
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class FuuidFormatterSpec extends AnyWordSpec with Matchers with FuuidFormatter {

  "FuuidFormatter" when {

    "call as json" must {

      "respond with fuuid json string" in {
        val fuuid = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")

        fuuid.asJson.noSpaces shouldBe s""""${fuuid.show}""""
      }
    }

    "call decode" must {

      "respond with fuuid from string" in {
        val string = "8ea16e29-3978-4113-8a06-eca8228f78ff"
        val json   = s""""$string""""

        decode[FUUID](json) shouldBe FUUID.fromString(string)
      }
    }
  }
}
