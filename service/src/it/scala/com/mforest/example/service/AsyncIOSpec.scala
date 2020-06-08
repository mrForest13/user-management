package com.mforest.example.service

import cats.effect.testing.scalatest.{AssertingSyntax, EffectTestSupport}
import cats.effect.{ContextShift, IO, Timer}
import org.scalatest.AsyncTestSuite

import scala.concurrent.ExecutionContext

trait AsyncIOSpec extends AssertingSyntax with EffectTestSupport {
  this: AsyncTestSuite =>

  override val executionContext: ExecutionContext = ExecutionContext.global

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val ioTimer: Timer[IO]               = IO.timer(executionContext)
}
