package com.mforest.example.core.formatter

import java.time.Instant

import io.circe.{Decoder, Encoder}

import scala.util.Try

trait InstantFormatter {

  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emapTry(str => Try(Instant.parse(str)))
}
