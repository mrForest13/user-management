package com.mforest.example.service.dto

import com.mforest.example.service.converter.DtoConverter.Converter
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sup.Health
import sup.data.Tagged

final case class CheckDto(service: String, healthy: Boolean)

object CheckDto {

  implicit val converter: Converter[Tagged[String, Health], CheckDto] = { tagged =>
    CheckDto(tagged.tag, tagged.health.isHealthy)
  }

  implicit val encoder: Encoder[CheckDto] = deriveEncoder
  implicit val decoder: Decoder[CheckDto] = deriveDecoder
}
