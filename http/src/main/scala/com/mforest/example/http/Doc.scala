package com.mforest.example.http

import com.mforest.example.http.token.BarerToken
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Codec, Tapir}

trait Doc extends Tapir with TapirJsonCirce {

  implicit val codecForBarerToken: PlainCodec[BarerToken] =
    Codec.stringPlainCodecUtf8.map(BarerToken.apply)(_.toString)
}
