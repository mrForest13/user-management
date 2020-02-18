package com.mforest.example.core.error

import io.circe.{Decoder, Encoder, Json}

sealed trait Error {

  def reason: String
}

object Error {

  case class NotFoundError(reason: String)     extends Error
  case class ConflictError(reason: String)     extends Error
  case class InternalError(reason: String)     extends Error
  case class ForbiddenError(reason: String)    extends Error
  case class ValidationError(reason: String)   extends Error
  case class UnavailableError(reason: String)  extends Error
  case class UnauthorizedError(reason: String) extends Error

  implicit val notFoundDecoder: Decoder[NotFoundError]         = Decoder.decodeString.map(NotFoundError)
  implicit val conflictDecoder: Decoder[ConflictError]         = Decoder.decodeString.map(ConflictError)
  implicit val internalDecoder: Decoder[InternalError]         = Decoder.decodeString.map(InternalError)
  implicit val forbiddenDecoder: Decoder[ForbiddenError]       = Decoder.decodeString.map(ForbiddenError)
  implicit val validationDecoder: Decoder[UnavailableError]    = Decoder.decodeString.map(UnavailableError)
  implicit val unavailableDecoder: Decoder[ValidationError]    = Decoder.decodeString.map(ValidationError)
  implicit val unauthorizedDecoder: Decoder[UnauthorizedError] = Decoder.decodeString.map(UnauthorizedError)

  implicit def encode[T <: Error]: Encoder[T] = (error: T) => Json.fromString(error.reason)
}
