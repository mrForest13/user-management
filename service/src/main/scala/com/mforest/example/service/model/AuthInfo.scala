package com.mforest.example.service.model

import cats.Id
import cats.data.NonEmptyChain
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.formatter.TokenFormatter
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import tsec.authentication.TSecBearerToken

final case class AuthInfo(identity: NonEmptyChain[PermissionDto], authenticator: TSecBearerToken[Id[FUUID]])

object AuthInfo extends TokenFormatter {

  implicit val encoder: Encoder[AuthInfo] = deriveEncoder
  implicit val decoder: Decoder[AuthInfo] = deriveDecoder
}
