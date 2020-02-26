package com.mforest.example.db.row

import cats.Id
import io.chrisdavenport.fuuid.FUUID

case class PermissionRow(id: Id[FUUID], name: String)
