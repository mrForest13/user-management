package com.mforest.example.core.formatter

import io.chrisdavenport.fuuid
import io.chrisdavenport.fuuid.FUUID
import io.circe.{Decoder, Encoder}

trait FuuidFormatter {

  implicit val fuuidEncoder: Encoder[FUUID] = fuuid.circe.fuuidEncoder
  implicit val fuuidDecoder: Decoder[FUUID] = fuuid.circe.fuuidDecoder
}
