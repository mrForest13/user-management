package com.mforest.example.db.custom

import doobie.postgres.Instances
import doobie.util.meta.Meta
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits.FuuidType

private[db] trait CustomInstances extends Instances {

  implicit val fuuidType: Meta[FUUID] = FuuidType
}
