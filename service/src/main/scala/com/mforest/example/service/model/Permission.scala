package com.mforest.example.service.model

import com.mforest.example.db.row.PermissionRow
import io.chrisdavenport.fuuid.FUUID

final case class Permission(name: String) {

  def toRow(id: FUUID): PermissionRow = {
    PermissionRow(id, name)
  }
}
