package com.mforest.example.core.error

import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Encoder, Json}

sealed trait Error {
  def reason: String
}

object Error {

  case class ConflictError(reason: String)    extends Error
  case class InternalError(reason: String)    extends Error
  case class ValidationError(reason: String)  extends Error
  case class UnavailableError(reason: String) extends Error

  implicit val conflictDecoder: Decoder[ConflictError]       = deriveDecoder
  implicit val internalDecoder: Decoder[InternalError]       = deriveDecoder
  implicit val validationDecoder: Decoder[UnavailableError]  = deriveDecoder
  implicit val unavailableDecoder: Decoder[ValidationError] = deriveDecoder

  implicit def encode[T <: Error]: Encoder[T] = (error: T) => Json.fromString(error.reason)
}
