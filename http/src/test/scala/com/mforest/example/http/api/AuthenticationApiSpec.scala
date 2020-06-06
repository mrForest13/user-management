package com.mforest.example.http.api

import java.time.Instant

import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.implicits.{catsSyntaxOptionId, toShow}
import com.mforest.example.core.error.Error
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.support.AuthorizationSupport
import com.mforest.example.http.token.{BasicToken, BearerToken}
import com.mforest.example.http.{Api, HttpSpec}
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.login.LoginService
import com.mforest.example.service.model.{Credentials, SessionInfo}
import io.chrisdavenport.fuuid.FUUID
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Header, Headers, MediaType, Method, Request, Response, Status}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class AuthenticationApiSpec
    extends AsyncWordSpec
    with HttpSpec
    with AuthorizationSupport[IO]
    with AsyncMockFactory
    with Matchers {

  val authService: AuthService[IO] = stub[AuthService[IO]]

  private val loginService: LoginService[IO] = stub[LoginService[IO]]

  private val api: Api[IO] = AuthenticationApi(loginService, authService)

  "AuthorizationApi" when {

    "call login user api" must {

      "respond with bearer token" in {
        val userId   = randomUnsafeId
        val randomId = SecureRandomId.Strong.generate

        val response = login(randomId, userId, EitherT.rightT(userId))

        checkOk[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Ok
            body shouldBe StatusResponse.Ok[String]("Login succeeded!")
            headers shouldBe Headers.of(
              Header("Authorization", new BearerToken(randomId).show),
              `Content-Type`(MediaType.application.json)
            )
        }
      }

      "respond with bad request error" in {
        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/api/login")
        )

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.BadRequest
            body shouldBe StatusResponse.Fail[String]("Invalid value for: header Authorization")
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unauthorized error" in {
        val userId   = randomUnsafeId
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.UnauthorizedError("Something went wrong!")

        val response = login(randomId, userId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Unauthorized
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unavailable error" in {
        val userId   = randomUnsafeId
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.UnavailableError("Something went wrong!")

        val response = login(randomId, userId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.ServiceUnavailable
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with internal error" in {
        val userId   = randomUnsafeId
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.InternalError("Something went wrong!")

        val response = login(randomId, userId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.InternalServerError
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      def login(id: SecureRandomId, userId: FUUID, result: EitherT[IO, Error, FUUID]): IO[Response[IO]] = {
        val login    = "admin@example.com"
        val password = "admin"
        val basic    = BasicToken("YWRtaW5AZXhhbXBsZS5jb206YWRtaW4=")
        val bearer = TSecBearerToken(
          id = id,
          identity = userId,
          expiry = Instant.now,
          lastTouched = Instant.now.some
        )

        (loginService.login _)
          .when(Credentials(login, password))
          .once()
          .returns(result)

        (authService.create _)
          .when(userId)
          .returns(IO.pure(bearer))

        api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/api/login")
            .withHeaders(Header("Authorization", basic.show))
        )
      }
    }

    "call logout user api" must {

      "respond with logout message" in {
        val randomId = SecureRandomId.Strong.generate
        val dto      = PermissionDto(randomUnsafeId, "EXAMPLE_PERMISSION")
        val bearer = TSecBearerToken(
          id = randomId,
          identity = randomUnsafeId,
          expiry = Instant.now,
          lastTouched = Instant.now.some
        )

        val result = SessionInfo(NonEmptyChain.one(dto), bearer)

        val response = logout(randomId, EitherT.rightT(result))

        checkOk[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Ok
            body shouldBe StatusResponse.Ok[String]("Logout succeeded!")
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with bad request error" in {
        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.DELETE, uri = uri"/api/logout")
        )

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.BadRequest
            body shouldBe StatusResponse.Fail[String]("Invalid value for: header Authorization")
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with forbidden error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.ForbiddenError("Something went wrong!")

        val response = logout(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Forbidden
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unavailable error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.UnavailableError("Something went wrong!")

        val response = logout(randomId, EitherT.leftT(result))

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

        val response = logout(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.InternalServerError
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      def logout(id: SecureRandomId, result: EitherT[IO, Error, SessionInfo]): IO[Response[IO]] = {
        val token = BearerToken(id)
        val bearer = TSecBearerToken(
          id = id,
          identity = randomUnsafeId,
          expiry = Instant.now,
          lastTouched = Instant.now.some
        )

        (authService.validateAndRenew _)
          .when(id)
          .once()
          .returns(result)

        (authService.discard _)
          .when(*)
          .returns(IO.pure(bearer))

        api.routes.orNotFound.run(
          Request[IO](method = Method.DELETE, uri = uri"/api/logout")
            .withHeaders(Header("Authorization", token.show))
        )
      }
    }
  }
}
