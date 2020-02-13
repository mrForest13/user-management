package com.mforest.example.db.custom

import cats.data.Chain
import doobie.util.compat.FactoryCompat

import scala.collection.{GenSeq, mutable}

trait CustomCompacts {

  implicit def chainCompact[Row]: FactoryCompat[Row, Chain[Row]] = new FactoryCompat[Row, Chain[Row]] {

    override def newBuilder: mutable.Builder[Row, Chain[Row]] = {
      GenSeq.newBuilder[Row].mapResult(Chain.fromSeq)
    }
  }
}
