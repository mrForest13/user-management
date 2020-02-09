package com.mforest.example.http

import sttp.tapir.Tapir
import sttp.tapir.json.circe.TapirJsonCirce

trait Doc extends Tapir with TapirJsonCirce
