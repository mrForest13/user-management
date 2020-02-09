package com.mforest.example.http.response

import io.circe.{Decoder, Encoder}

object ResponseType extends Enumeration {

  type Type = Value

  val OK: Type   = Value("Ok")
  val FAIL: Type = Value("Fail")

  implicit val decoder: Decoder[Type] = Decoder.decodeEnumeration(ResponseType)
  implicit val encoder: Encoder[Type] = Encoder.encodeEnumeration(ResponseType)
}
