package com.mforest.example.db.row

import cats.Id
import io.chrisdavenport.fuuid.FUUID

final case class PermissionRow(id: Id[FUUID], name: String)
