package com.mforest.example.core.formatter

import java.time.Instant

import cats.implicits.catsSyntaxEitherId
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class InstantFormatterSpec extends AnyWordSpec with Matchers with InstantFormatter {

  "InstantFormatter" when {

    "call as json" must {

      "respond with millis json number" in {
        val date    = "2018-11-30T18:35:24.00Z"
        val instant = Instant.parse(date)

        instant.asJson.noSpaces shouldBe """1543602924000"""
      }
    }

    "call decode" must {

      "respond with instant from milliseconds" in {
        val date    = "2018-11-30T18:35:24.00Z"
        val json    = """"1543602924000""""
        val instant = Instant.parse(date)

        decode[Instant](json) shouldBe instant.asRight
      }
    }
  }
}
