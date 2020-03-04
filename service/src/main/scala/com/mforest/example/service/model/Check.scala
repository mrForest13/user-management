package com.mforest.example.service.model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class Check(service: String, healthy: Boolean)

object Check {

  implicit val encoder: Encoder[Check] = deriveEncoder
  implicit val decoder: Decoder[Check] = deriveDecoder
}