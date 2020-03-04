package com.mforest.example.http

import cats.syntax.OptionSyntax
import com.mforest.example.http.response.StatusResponse
import com.mforest.example.http.token.BearerToken
import io.chrisdavenport.fuuid.FUUID
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.SchemaType.SString
import sttp.tapir.codec.cats.TapirCodecCats
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Codec, Endpoint, Schema, Tapir}
import tsec.common.SecureRandomId

private[http] trait Doc extends Tapir with TapirJsonCirce with TapirCodecCats with OptionSyntax {

  def endpoints: Seq[Endpoint[_, _, _, _]]

  type Ok[T]   = StatusResponse.Ok[T]
  type Fail[T] = StatusResponse.Fail[T]

  type Token            = String
  type PaginationParams = (Option[Int], Option[Int], Token)

  implicit val schemaForFuuid: Schema[FUUID]       = Schema(SString)
  implicit val schemaForId: Schema[SecureRandomId] = Schema(SString)

  implicit val fuuidPlainCodec: PlainCodec[FUUID] =
    Codec.uuidPlainCodec.map(FUUID.fromUUID)(FUUID.Unsafe.toUUID)
  implicit val codecForBarerToken: PlainCodec[BearerToken] =
    Codec.stringPlainCodecUtf8.map(BearerToken.apply)(_.toString)
}
