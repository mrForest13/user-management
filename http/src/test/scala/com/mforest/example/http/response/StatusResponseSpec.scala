package com.mforest.example.http.response

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import io.circe.Decoder
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class StatusResponseSpec extends AnyWordSpec with Matchers {

  "StatusResponse" when {

    "call as json" must {

      "respond with ok response json string" in {
        val data     = "example"
        val response = StatusResponse.ok(data)

        response.asJson.noSpaces shouldBe """{"status":"Ok","data":"example"}"""
      }

      "respond with fail response json string" in {
        val data     = "example"
        val response = StatusResponse.fail(data)

        response.asJson.noSpaces shouldBe """{"status":"Fail","data":"example"}"""
      }
    }

    "call decode" must {

      "respond with ok status response" in {
        val data     = "example"
        val json     = """{"data":"example"}"""
        val response = StatusResponse.ok(data)

        decode[StatusResponse.Ok[String]](json) shouldBe response.asRight
      }

      "respond with fail response json string" in {
        val data     = "example"
        val json     = """{"data":"example"}"""
        val response = StatusResponse.fail(data)

        decode[StatusResponse.Fail[String]](json) shouldBe response.asRight
      }
    }
  }
}

object StatusResponseSpec {

  implicit def encoderOk[T: Decoder]: EntityDecoder[IO, StatusResponse.Ok[T]] =
    jsonOf[IO, StatusResponse.Ok[T]]

  implicit def encoderFail[T: Decoder]: EntityDecoder[IO, StatusResponse.Fail[T]] =
    jsonOf[IO, StatusResponse.Fail[T]]
}
