package com.mforest.example.http.support

import java.sql.SQLException

import cats.data.EitherT
import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.mforest.example.core.error.Error.{InternalError, NotFoundError, UnavailableError}
import com.mforest.example.http.HttpSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class ErrorHandlerSupportSpec extends AsyncWordSpec with HttpSpec with ErrorHandlerSupport with Matchers {

  "ErrorHandlerSupport" when {

    "call handleError" must {

      "respond with valid data" in {
        val data = "example"

        val result = handleError[IO, String](EitherT.rightT(data))

        result.value.asserting(_ shouldBe data.asRight)
      }

      "respond with not found error" in {
        val error = NotFoundError("Cannot found error!")

        val result = handleError[IO, String](EitherT.leftT(error))

        result.value.asserting(_ shouldBe error.asLeft)
      }

      "respond with unavailable error for sql exception" in {
        val data = IO.raiseError(new SQLException("Example error"))

        val result = handleError[IO, String](EitherT.liftF(data))

        val error = UnavailableError("The server is currently unavailable!")

        result.value.asserting(_ shouldBe error.asLeft)
      }

      "respond with internal error for any exception" in {
        val data = IO.raiseError(new RuntimeException("Example error"))

        val result = handleError[IO, String](EitherT.liftF(data))

        val error = InternalError("There was an internal server error!")

        result.value.asserting(_ shouldBe error.asLeft)
      }
    }
  }
}
