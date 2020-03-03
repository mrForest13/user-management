package com.mforest.example.service.formatter

import cats.Id
import com.mforest.example.core.formatter.{FuuidFormatter, InstantFormatter}
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

trait TokenFormatter extends FuuidFormatter with InstantFormatter {

  implicit val secureRandomIdEncoder: Encoder[SecureRandomId] =
    Encoder.encodeString.asInstanceOf[Encoder[SecureRandomId]]
  implicit val secureRandomIdDecoder: Decoder[SecureRandomId] =
    Decoder.decodeString.asInstanceOf[Decoder[SecureRandomId]]

  implicit val tokenEncoder: Encoder[TSecBearerToken[Id[FUUID]]] = deriveEncoder
  implicit val tokenDecoder: Decoder[TSecBearerToken[Id[FUUID]]] = deriveDecoder
}
