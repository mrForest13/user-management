package com.mforest.example.db.instances

import java.util.UUID

import doobie.postgres.Instances
import doobie.util.Meta
import io.chrisdavenport.fuuid.FUUID

trait CustomInstances extends Instances {

  implicit def fuuidType(implicit U: Meta[UUID]): Meta[FUUID] =
    U.timap[FUUID](FUUID.fromUUID)(fuuid => FUUID.Unsafe.toUUID(fuuid))
}
