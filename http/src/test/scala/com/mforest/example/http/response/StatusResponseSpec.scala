package com.mforest.example.http.response

import cats.effect.IO
import io.circe.Decoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

class StatusResponseSpec {

}

object StatusResponseSpec {

  implicit def encoderOk[T: Decoder]: EntityDecoder[IO, StatusResponse.Ok[T]] =
    jsonOf[IO, StatusResponse.Ok[T]]

  implicit def encoderFail[T: Decoder]: EntityDecoder[IO, StatusResponse.Fail[T]] =
    jsonOf[IO, StatusResponse.Fail[T]]
}