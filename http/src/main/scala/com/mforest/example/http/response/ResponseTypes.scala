package com.mforest.example.http.response

import io.circe.{Decoder, Encoder}

private[response] object ResponseTypes extends Enumeration {

  type Type = Value

  val OK: Type   = Value("Ok")
  val FAIL: Type = Value("Fail")

  implicit val decoder: Decoder[Type] = Decoder.decodeEnumeration(ResponseTypes)
  implicit val encoder: Encoder[Type] = Encoder.encodeEnumeration(ResponseTypes)
}
