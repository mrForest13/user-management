package com.mforest.example.http

import cats.effect.testing.scalatest.{AssertingSyntax, EffectTestSupport}
import cats.effect.{ContextShift, IO, Timer}
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.response.StatusResponseSpec.{encoderFail, encoderOk}
import io.chrisdavenport.fuuid.FUUID
import io.circe.Decoder
import org.http4s.{Headers, Response, Status}
import org.scalatest.AsyncTestSuite

import scala.concurrent.ExecutionContext

trait HttpSpec extends AssertingSyntax with EffectTestSupport {
  this: AsyncTestSuite =>

  override val executionContext: ExecutionContext = ExecutionContext.global

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val ioTimer: Timer[IO]               = IO.timer(executionContext)

  def checkOk[R: Decoder](response: IO[Response[IO]]): IO[(Status, Headers, StatusResponse.Ok[R])] = {
    response.flatMap { result =>
      result.as[StatusResponse.Ok[R]].map((result.status, result.headers, _))
    }
  }

  def checkFail[E: Decoder](response: IO[Response[IO]]): IO[(Status, Headers, StatusResponse.Fail[E])] = {
    response.flatMap { result =>
      result.as[StatusResponse.Fail[E]].map((result.status, result.headers, _))
    }
  }

  def randomUnsafeId: FUUID = {
    FUUID.randomFUUID[IO].unsafeRunSync()
  }
}
