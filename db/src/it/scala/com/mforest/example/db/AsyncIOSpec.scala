package com.mforest.example.db

import cats.effect.testing.scalatest.{AssertingSyntax, EffectTestSupport}
import cats.effect.{ContextShift, IO, Timer}
import org.scalatest.AsyncTestSuite

trait AsyncIOSpec extends AssertingSyntax with EffectTestSupport {
  this: AsyncTestSuite =>

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val ioTimer: Timer[IO] = IO.timer(executionContext)
}
