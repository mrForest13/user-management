package com.mforest.example.service.dto

import cats.data.{Chain, NonEmptyList}
import com.mforest.example.service.converter.DtoConverter.{ChainConverter, NonEmptyListConverter}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sup.Health
import sup.data.Tagged

final class HealthCheckDtoSpec extends AnyWordSpec with Matchers {

  "HealthCheckTagged" when {

    "chain to" must {

      "respond with health check dto" in {
        val name   = "Example"
        val tagged = Chain.one(Tagged(name, Health.healthy))
        val result = Chain.one(HealthCheckDto(name, healthy = true))

        tagged.to[HealthCheckDto] shouldBe result
      }
    }

    "non empty list to" must {

      "respond with health check dto" in {
        val name   = "Example"
        val tagged = NonEmptyList.one(Tagged(name, Health.healthy))
        val result = NonEmptyList.one(HealthCheckDto(name, healthy = true))

        tagged.to[HealthCheckDto] shouldBe result
      }
    }
  }
}
