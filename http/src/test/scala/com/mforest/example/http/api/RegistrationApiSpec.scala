package com.mforest.example.http.api

import cats.data.EitherT
import cats.effect.IO
import com.mforest.example.core.error.Error
import com.mforest.example.http.form.RegistrationFormSpec
import com.mforest.example.http.form.RegistrationFormSpec.encoder
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.{Api, HttpSpec}
import com.mforest.example.service.registration.RegistrationService
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Headers, MediaType, Method, Request, Response, Status}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class RegistrationApiSpec extends AsyncWordSpec with HttpSpec with AsyncMockFactory with Matchers {

  private val service: RegistrationService[IO] = stub[RegistrationService[IO]]

  private val api: Api[IO] = RegistrationApi(service)

  "RegistrationApi" when {

    "call register user api" must {

      "respond with success message" in {
        val result = "The user has been added!"

        val response: IO[Response[IO]] = registerUser(EitherT.rightT(result))

        checkOk[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Created
            body shouldBe StatusResponse.Ok[String](result)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with conflict error" in {
        val result = Error.ConflictError("Something went wrong!")

        val response: IO[Response[IO]] = registerUser(EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Conflict
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with bad request error" in {
        val result = Error.ValidationError("Something went wrong!")

        val response: IO[Response[IO]] = registerUser(EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.BadRequest
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unavailable request error" in {
        val result = Error.UnavailableError("Something went wrong!")

        val response: IO[Response[IO]] = registerUser(EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.ServiceUnavailable
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with internal error" in {
        val result = Error.InternalError("Something went wrong!")

        val response: IO[Response[IO]] = registerUser(EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.InternalServerError
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      def registerUser(result: EitherT[IO, Error, String]): IO[Response[IO]] = {
        val body = RegistrationFormSpec.formMock

        (service.register _).when(body.toDto).once().returns(result)

        api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/users")
            .withEntity(body)
        )
      }
    }
  }
}
