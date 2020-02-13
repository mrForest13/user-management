package com.mforest.example.http

import java.util.UUID

import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import io.chrisdavenport.fuuid.FUUID
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.SchemaType.SString
import sttp.tapir.codec.cats.TapirCodecCats
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Codec, Schema, Tapir}

trait Doc extends Tapir with TapirJsonCirce with TapirCodecCats {

  type Ok[T]   = StatusResponse.Ok[T]
  type Fail[T] = StatusResponse.Fail[T]

  implicit val schemaForFuuid: Schema[FUUID] = Schema(SString)

  implicit val uuidPlainCodec: PlainCodec[UUID] = Codec.uuidPlainCodec
  implicit val fuuidPlainCodec: PlainCodec[FUUID] =
    Codec.uuidPlainCodec.map(FUUID.fromUUID)(FUUID.Unsafe.toUUID)
  implicit val codecForBarerToken: PlainCodec[BarerToken] =
    Codec.stringPlainCodecUtf8.map(BarerToken.apply)(_.toString)
}
