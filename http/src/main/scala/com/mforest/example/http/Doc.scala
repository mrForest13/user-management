package com.mforest.example.http

import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Endpoint, Tapir}

trait Doc extends Tapir with TapirJsonCirce {

  def docs: Seq[Endpoint[_, _, _, _]]
}
