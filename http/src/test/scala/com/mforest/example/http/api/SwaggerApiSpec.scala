package com.mforest.example.http.api

import cats.effect.IO
import com.mforest.example.core.config.swagger.SwaggerConfig
import com.mforest.example.http.{Api, HttpSpec}
import org.http4s.headers.{`Content-Length`, `Content-Type`}
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Charset, Headers, MediaType, Method, Request, Status}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class SwaggerApiSpec extends AsyncWordSpec with HttpSpec with Matchers {

  private val yaml   = "Example yaml"
  private val config = SwaggerConfig("docs", "docs.yaml", Map.empty)

  private val api: Api[IO] = SwaggerApi(yaml, config)

  "SwaggerApi" when {

    "call yaml api" must {

      "respond with yaml string" in {
        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.GET, uri = uri"/docs/docs.yaml")
        )

        response.flatMap { result =>
          result.as[String].map((result.status, result.headers, _)).asserting {
            case (status, headers, body) =>
              status shouldBe Status.Ok
              body shouldBe yaml
              headers shouldBe Headers.of(
                `Content-Type`(MediaType.text.plain, Charset.`UTF-8`),
                `Content-Length`.unsafeFromLong(length = 12)
              )
          }
        }
      }
    }
  }
}
