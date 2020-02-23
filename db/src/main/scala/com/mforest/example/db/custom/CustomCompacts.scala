package com.mforest.example.db.custom

import cats.data.Chain
import doobie.util.compat.FactoryCompat

import scala.collection.mutable

private[db] trait CustomCompacts {

  implicit def chainCompact[Row]: FactoryCompat[Row, Chain[Row]] = new FactoryCompat[Row, Chain[Row]] {

    override def newBuilder: mutable.Builder[Row, Chain[Row]] = {
      Seq.newBuilder[Row].mapResult(Chain.fromSeq)
    }
  }
}
