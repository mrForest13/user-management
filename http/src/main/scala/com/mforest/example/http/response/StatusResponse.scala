package com.mforest.example.http.response

import io.circe.{Decoder, Encoder}

sealed abstract class StatusResponse[+T](val status: ResponseType.Type) {
  def data: T
}

object StatusResponse {

  def ok[T](data: T): Ok[T]     = Ok(data)
  def fail[T](data: T): Fail[T] = Fail(data)

  case class Ok[+T](data: T)   extends StatusResponse[T](ResponseType.OK)
  case class Fail[+T](data: T) extends StatusResponse[T](ResponseType.FAIL)

  implicit def okDecoder[T: Decoder]: Decoder[StatusResponse.Ok[T]] =
    Decoder.forProduct1("data")((data: T) => StatusResponse.Ok[T](data))
  implicit def okEncoder[T: Encoder]: Encoder[StatusResponse.Ok[T]] =
    Encoder.forProduct2("status", "data")(response => (response.status, response.data))

  implicit def failDecoder[T: Decoder]: Decoder[StatusResponse.Fail[T]] =
    Decoder.forProduct1("data")((data: T) => StatusResponse.Fail[T](data))
  implicit def failEncoder[T: Encoder]: Encoder[StatusResponse.Fail[T]] =
    Encoder.forProduct2("status", "data")(response => (response.status, response.data))
}
