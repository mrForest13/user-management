package com.mforest.example.core.formatter

import java.time.Instant

import io.circe.{Decoder, Encoder}

import scala.util.Try

trait InstantFormatter {

  private val millisToInstant = (millis: Long) => Try(Instant.ofEpochMilli(millis))

  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeLong.contramap(_.toEpochMilli)
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeLong.emapTry(millisToInstant)
}
