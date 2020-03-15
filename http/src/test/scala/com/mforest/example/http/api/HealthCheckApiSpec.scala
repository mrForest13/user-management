package com.mforest.example.http.api

import cats.data.{EitherT, NonEmptyList}
import cats.effect.IO
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.{Api, HttpSpec}
import com.mforest.example.service.dto.HealthCheckDto
import com.mforest.example.service.health.HealthCheckService
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Headers, MediaType, Method, Request, Status}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class HealthCheckApiSpec extends AsyncWordSpec with HttpSpec with AsyncMockFactory with Matchers {

  private val service: HealthCheckService[IO] = mock[HealthCheckService[IO]]

  private val api: Api[IO] = HealthCheckApi(service)

  "HealthCheckApi" when {

    "call health check api" must {

      "respond with check info" in {
        val result = NonEmptyList.of(
          HealthCheckDto(service = "database", healthy = true),
          HealthCheckDto(service = "cache", healthy = true)
        )

        (service.check _).expects().once().returns(EitherT.rightT(result))

        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.GET, uri = uri"/api/health-check")
        )

        checkOk[NonEmptyList[HealthCheckDto]](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Ok
            body shouldBe StatusResponse.Ok[NonEmptyList[HealthCheckDto]](result)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unavailable request error" in {
        val result = Error.UnavailableError("Something went wrong!")

        (service.check _).expects().once().returns(EitherT.leftT(result))

        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.GET, uri = uri"/api/health-check")
        )

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.ServiceUnavailable
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with internal error" in {
        val result = Error.InternalError("Something went wrong!")

        (service.check _).expects().once().returns(EitherT.leftT(result))

        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.GET, uri = uri"/api/health-check")
        )

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.InternalServerError
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }
    }
  }
}
