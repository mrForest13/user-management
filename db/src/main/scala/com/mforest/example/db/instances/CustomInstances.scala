package com.mforest.example.db.instances

import doobie.postgres.Instances
import doobie.util.Meta
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits.FuuidType

trait CustomInstances extends Instances {

  implicit def fuuidType: Meta[FUUID] = FuuidType
}
