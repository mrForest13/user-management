package com.mforest.example.http.api

import java.time.Instant

import cats.Show
import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.implicits.{catsSyntaxOptionId, toShow}
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
import org.http4s.{Header, Headers, MediaType, Method, Request, Status}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

class PermissionApiSpec
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
        val form       = AddPermissionForm(permission)
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

        (authService
          .authorize(_: String, _: Permission)(_: Show[Permission]))
          .when(randomId, USER_MANAGEMENT_ADD_PERMISSION, show)
          .once()
          .returns(EitherT.rightT(result))

        (permissionService.addPermission _)
          .when(form.toDto)
          .returns(EitherT.rightT(createdMsg))

        val response = api.routes.orNotFound.run(
          Request[IO](method = Method.POST, uri = uri"/permissions")
            .withHeaders(Header("Authorization", token.show))
            .withEntity(form)
        )

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
    }
  }
}
