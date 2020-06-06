package com.mforest.example.http.api

import java.time.Instant

import cats.Show
import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.implicits.{catsSyntaxOptionId, toShow}
import com.mforest.example.core.error.Error
import com.mforest.example.core.permission.Permissions._
import com.mforest.example.http.form.AddPermissionForm
import com.mforest.example.http.form.AddPermissionFormSpec.encoder
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.support.AuthorizationSupport
import com.mforest.example.http.token.BearerToken
import com.mforest.example.http.{Api, HttpSpec}
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.SessionInfo
import com.mforest.example.service.permission.PermissionService
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Header, Headers, MediaType, Method, Request, Response, Status}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class PermissionApiSpec
    extends AsyncWordSpec
    with HttpSpec
    with AuthorizationSupport[IO]
    with AsyncMockFactory
    with Matchers {

  val authService: AuthService[IO] = stub[AuthService[IO]]

  private val permissionService: PermissionService[IO] = stub[PermissionService[IO]]

  private val api: Api[IO] = PermissionApi(permissionService, authService)

  "PermissionApi" when {

    "call add permission api" must {

      "respond with success message and token" in {
        val randomId   = SecureRandomId.Strong.generate
        val permission = "EXAMPLE_PERMISSION"
        val token      = new BearerToken(randomId)
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

        val createdMsg = "The permission has been created!"

        val response = addPermission(randomId, EitherT.rightT(result))

        checkOk[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Created
            body shouldBe StatusResponse.Ok[String](createdMsg)
            headers shouldBe Headers.of(
              Header("Authorization", token.show),
              `Content-Type`(MediaType.application.json)
            )
        }
      }

      "respond with conflict error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.ConflictError("Something went wrong!")

        val response: IO[Response[IO]] = addPermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.Conflict
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with forbidden error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.ForbiddenError("Something went wrong!")

        val response: IO[Response[IO]] = addPermission(randomId, EitherT.leftT(result))

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

        val response: IO[Response[IO]] = addPermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.BadRequest
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      "respond with unavailable error" in {
        val randomId = SecureRandomId.Strong.generate
        val result   = Error.UnavailableError("Something went wrong!")

        val response: IO[Response[IO]] = addPermission(randomId, EitherT.leftT(result))

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

        val response: IO[Response[IO]] = addPermission(randomId, EitherT.leftT(result))

        checkFail[String](response).asserting {
          case (status, headers, body) =>
            status shouldBe Status.InternalServerError
            body shouldBe StatusResponse.Fail[String](result.reason)
            headers shouldBe Headers.of(`Content-Type`(MediaType.application.json))
        }
      }

      def addPermission(id: SecureRandomId, result: EitherT[IO, Error, SessionInfo]): IO[Response[IO]] = {
        val permission = "EXAMPLE_PERMISSION"
        val token      = new BearerToken(id)
        val form       = AddPermissionForm(permission)

        val createdMsg = "The permission has been created!"

        (authService
          .authorize(_: String, _: Permission)(_: Show[Permission]))
          .when(id, USER_MANAGEMENT_ADD_PERMISSION, show)
          .once()
          .returns(result)

        (permissionService.addPermission _)
          .when(form.toDto)
          .returns(EitherT.rightT(createdMsg))

        api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/permissions")
            .withHeaders(Header("Authorization", token.show))
            .withEntity(form)
        )
      }
    }
  }
}
