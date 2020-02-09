package com.mforest.example.core

import cats.effect.IO
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ConfigLoaderSpec extends AnyWordSpec with Matchers {

  "ConfigLoader" when {

    "call load" must {

      "respond with database config" in {

        ConfigLoader[IO].load
      }
    }
  }
}
