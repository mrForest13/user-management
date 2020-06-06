package com.mforest.example.service.dto

import com.mforest.example.service.converter.DtoConverter.Converter
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sup.Health
import sup.data.Tagged

final case class HealthCheckDto(service: String, healthy: Boolean)

object HealthCheckDto {

  implicit val converter: Converter[Tagged[String, Health], HealthCheckDto] = { tagged =>
    HealthCheckDto(tagged.tag, tagged.health.isHealthy)
  }

  implicit val encoder: Encoder[HealthCheckDto] = deriveEncoder
  implicit val decoder: Decoder[HealthCheckDto] = deriveDecoder
}
