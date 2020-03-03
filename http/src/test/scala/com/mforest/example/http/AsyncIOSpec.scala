package com.mforest.example.http

import cats.effect.testing.scalatest.{AssertingSyntax, EffectTestSupport}
import cats.effect.{ContextShift, IO, Timer}
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.response.StatusResponseSpec.{encoderFail, encoderOk}
import org.http4s.{Headers, Response, Status}
import org.scalatest.AsyncTestSuite

import scala.concurrent.ExecutionContext

trait AsyncIOSpec extends AssertingSyntax with EffectTestSupport {
  this: AsyncTestSuite =>

  override val executionContext: ExecutionContext = ExecutionContext.global

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val ioTimer: Timer[IO]               = IO.timer(executionContext)

  def checkOk[R](response: IO[Response[IO]]): IO[(Status, Headers, StatusResponse.Ok[String])] = {
    response.flatMap { result =>
      result.as[StatusResponse.Ok[String]].map((result.status, result.headers, _))
    }
  }

  def checkFail(response: IO[Response[IO]]): IO[(Status, Headers, StatusResponse.Fail[String])] = {
    response.flatMap { result =>
      result.as[StatusResponse.Fail[String]].map((result.status, result.headers, _))
    }
  }
}
