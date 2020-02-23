package com.mforest.example.http.response

import io.circe.{Decoder, Encoder}

sealed abstract class StatusResponse[+T](val status: ResponseTypes.Type) {
  def data: T
}

object StatusResponse {

  def ok[T](data: T): Ok[T]     = Ok(data)
  def fail[T](data: T): Fail[T] = Fail(data)

  case class Ok[+T](data: T)   extends StatusResponse[T](ResponseTypes.OK)
  case class Fail[+T](data: T) extends StatusResponse[T](ResponseTypes.FAIL)

  private def tuple[T](response: StatusResponse[T]): (ResponseTypes.Type, T) = {
    (response.status, response.data)
  }

  implicit def okDecoder[T: Decoder]: Decoder[StatusResponse.Ok[T]] =
    Decoder.forProduct1("data")(StatusResponse.Ok[T])
  implicit def okEncoder[T: Encoder]: Encoder[StatusResponse.Ok[T]] =
    Encoder.forProduct2("status", "data")(tuple[T](_))

  implicit def failDecoder[T: Decoder]: Decoder[StatusResponse.Fail[T]] =
    Decoder.forProduct1("data")(StatusResponse.Fail[T])
  implicit def failEncoder[T: Encoder]: Encoder[StatusResponse.Fail[T]] =
    Encoder.forProduct2("status", "data")(tuple[T](_))
}
