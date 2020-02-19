package com.mforest.example.http

import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BarerToken
import io.chrisdavenport.fuuid.FUUID
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.SchemaType.SString
import sttp.tapir.codec.cats.TapirCodecCats
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Codec, Endpoint, Schema, Tapir}

trait Doc extends Tapir with TapirJsonCirce with TapirCodecCats {

  def endpoints: Seq[Endpoint[_, _, _, _]]

  type Ok[T]   = StatusResponse.Ok[T]
  type Fail[T] = StatusResponse.Fail[T]

  implicit val schemaForFuuid: Schema[FUUID] = Schema(SString)

  implicit val fuuidPlainCodec: PlainCodec[FUUID] =
    Codec.uuidPlainCodec.map(FUUID.fromUUID)(FUUID.Unsafe.toUUID)
  implicit val codecForBarerToken: PlainCodec[BarerToken] =
    Codec.stringPlainCodecUtf8.map(BarerToken.apply)(_.toString)
}
