package com.mforest.example.service.model

import cats.Id
import cats.data.NonEmptyChain
import com.mforest.example.service.dto.PermissionDto
import com.mforest.example.service.formatter.TokenFormatter
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import tsec.authentication.{SecuredRequest, TSecBearerToken}

final case class SessionInfo(identity: NonEmptyChain[PermissionDto], authenticator: TSecBearerToken[Id[FUUID]])

object SessionInfo extends TokenFormatter {

  type Request[F[_]] = SecuredRequest[F, NonEmptyChain[PermissionDto], TSecBearerToken[Id[FUUID]]]

  def apply[F[_]](request: Request[F]): SessionInfo = {
    new SessionInfo(request.identity, request.authenticator)
  }

  implicit val encoder: Encoder[SessionInfo] = deriveEncoder
  implicit val decoder: Decoder[SessionInfo] = deriveDecoder
}
