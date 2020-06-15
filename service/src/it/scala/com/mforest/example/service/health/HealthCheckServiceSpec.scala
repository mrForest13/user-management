package com.mforest.example.service.health

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.mforest.example.core.config.health.{CheckConfig, HealthCheckConfig}
import com.mforest.example.service.ServiceSpec
import com.mforest.example.service.dto.HealthCheckDto
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.duration.DurationInt

final class HealthCheckServiceSpec extends AsyncWordSpec with ServiceSpec with Matchers {

  private val cache: CheckConfig              = CheckConfig("cache", 5.seconds)
  private val database: CheckConfig           = CheckConfig("database", 5.seconds)
  private val config: HealthCheckConfig       = HealthCheckConfig(database, cache)
  private val service: HealthCheckService[IO] = HealthCheckService(transactor, client, config)

  "HealthCheckService" when {

    "call check" must {

      "respond with healthy response" in {
        val result = NonEmptyList.of(
          HealthCheckDto(service = "database", healthy = true),
          HealthCheckDto(service = "cache", healthy = true)
        )

        service.check.value.asserting {
          _ shouldBe result.asRight
        }
      }
    }
  }
}
