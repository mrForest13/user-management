package com.mforest.example.http.api

import cats.data.EitherT
import cats.effect.IO
import com.mforest.example.core.error.Error
import com.mforest.example.http.form.RegistrationFormSpec
import com.mforest.example.http.form.RegistrationFormSpec.encoder
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.{Api, AsyncIOSpec}
import com.mforest.example.service.registration.RegistrationService
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s._
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class RegistrationApiSpec extends AsyncWordSpec with AsyncIOSpec with AsyncMockFactory with Matchers {

  private val service: RegistrationService[IO] = stub[RegistrationService[IO]]

  private val api: Api[IO] = RegistrationApi(service)

  "RegistrationApiSpec" when {

    "call register api" must {

      "respond with success message" in {
        val body = RegistrationFormSpec.formMock

        val response: IO[Response[IO]] = api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/users").withEntity(body)
        )

        val result = "The user has been added!"

        (service.register _).when(body.toDto).once().returns(EitherT.rightT[IO, Error](result))

        checkOk(response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Created
            body shouldBe StatusResponse.Ok[String](result)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with conflict error" in {
        val body = RegistrationFormSpec.formMock

        val response: IO[Response[IO]] = api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/users").withEntity(body)
        )

        val result: Error = Error.ConflictError("Something went wrong!")

        (service.register _).when(body.toDto).once().returns(EitherT.leftT[IO, String](result))

        checkFail(response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Conflict
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }
    }
  }
}
