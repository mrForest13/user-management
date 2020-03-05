package com.mforest.example.http.support

import java.time.Instant

import cats.Show
import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.implicits.{catsStdShowForString, catsSyntaxEitherId, catsSyntaxOptionId}
import com.mforest.example.core.error.Error
import com.mforest.example.http.HttpSpec
import com.mforest.example.http.token.BearerToken
import com.mforest.example.service.auth.AuthService
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.model.SessionInfo
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final class AuthorizationSupportSpec
    extends AsyncWordSpec
    with HttpSpec
    with AuthorizationSupport[IO]
    with AsyncMockFactory
    with Matchers {

  override val authService: AuthService[IO] = stub[AuthService[IO]]

  "AuthorizationSupport" when {

    "call authorize" must {

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

        (authService
          .authorize(_: String, _: String)(_: Show[String]))
          .when(randomId, permission, catsStdShowForString)
          .once()
          .returns(EitherT.rightT(result))

        authorize(randomId, permission)(EitherT.rightT(_)).value.asserting {
          _ shouldBe (BearerToken(randomId) -> result).asRight
        }
      }

      "respond with forbidden error" in {
        val randomId   = SecureRandomId.Strong.generate
        val permission = "EXAMPLE_PERMISSION"

        val result = Error.ForbiddenError("Something went wrong!")

        (authService
          .authorize(_: String, _: String)(_: Show[String]))
          .when(randomId, permission, catsStdShowForString)
          .once()
          .returns(EitherT.leftT(result))

        authorize(randomId, permission)(EitherT.rightT(_)).value.asserting {
          _ shouldBe result.asLeft
        }
      }
    }
  }
}
