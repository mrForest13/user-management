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
    Encoder.encodeString.contramap(SecureRandomId.apply)
  implicit val secureRandomIdDecoder: Decoder[SecureRandomId] =
    Decoder.decodeString.map(SecureRandomId.apply)

  implicit val tokenEncoder: Encoder[TSecBearerToken[Id[FUUID]]] = deriveEncoder
  implicit val tokenDecoder: Decoder[TSecBearerToken[Id[FUUID]]] = deriveDecoder
}
