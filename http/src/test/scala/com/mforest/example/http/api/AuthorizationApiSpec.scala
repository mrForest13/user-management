package com.mforest.example.http.api

import java.time.Instant

import cats.Show
import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.implicits.{catsStdShowForString, catsSyntaxOptionId, toShow}
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BearerToken
import com.mforest.example.http.{Api, HttpSpec}
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.SessionInfo
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Header, Headers, MediaType, Method, Request, Response, Status}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class AuthorizationApiSpec extends AsyncWordSpec with HttpSpec with AsyncMockFactory with Matchers {

  private val service: AuthService[IO] = stub[AuthService[IO]]

  private val api: Api[IO] = AuthorizationApi(service)

  "AuthorizationApi" when {

    "call validate permission api" must {

      "respond with sessions info" in {
        val randomId   = SecureRandomId.Strong.generate
        val permission = "EXAMPLE_PERMISSION"
        val dto        = PermissionDto(randomUnsafeId, permission)

        val result = SessionInfo(
          identity = NonEmptyChain.one(dto),
          authenticator = TSecBearerToken(
            id = randomId,
            identity = randomUnsafeId,
            expiry = Instant.now,
            lastTouched = Instant.now.some
          )
        )

        val response: IO[Response[IO]] = validatePermission(randomId, EitherT.rightT(result))

        checkOk[SessionInfo](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Ok
            body shouldBe StatusResponse.Ok[SessionInfo](result)
            headers shouldBe Headers.of(
              Header("Authorization", new BearerToken(randomId).show),
              `Content-Type`(MediaType.application.json)
            )
        }
      }

      "respond with not found error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.NotFoundError("Something went wrong!")

        val response: IO[Response[IO]] = validatePermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.NotFound
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with forbidden error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.ForbiddenError("Something went wrong!")

        val response: IO[Response[IO]] = validatePermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Forbidden
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with bad request error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.ValidationError("Something went wrong!")

        val response: IO[Response[IO]] = validatePermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.BadRequest
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unavailable request error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.UnavailableError("Something went wrong!")

        val response: IO[Response[IO]] = validatePermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.ServiceUnavailable
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with internal error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.InternalError("Something went wrong!")

        val response: IO[Response[IO]] = validatePermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.InternalServerError
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      def validatePermission(id: SecureRandomId, result: EitherT[IO, Error, SessionInfo]): IO[Response[IO]] = {
        val token      = new BearerToken(id)
        val permission = "EXAMPLE_PERMISSION"

        (service
          .authorize(_: String, _: String)(_: Show[String]))
          .when(id, permission, catsStdShowForString)
          .once()
          .returns(result)

        api.routes.orNotFound.run(
          Request[IO](method = Method.GET, uri = uri"/permissions/EXAMPLE_PERMISSION/validate")
            .withHeaders(Header("Authorization", token.show))
        )
      }
    }
  }
}
