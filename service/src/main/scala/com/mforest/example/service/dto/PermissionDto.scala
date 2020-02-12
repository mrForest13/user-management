package com.mforest.example.service.dto

import com.mforest.example.core.formatter.FuuidFormatter
import com.mforest.example.db.row.PermissionRow
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class PermissionDto(id: FUUID, name: String)

object PermissionDto extends FuuidFormatter {

  def fromRow(row: PermissionRow): PermissionDto = PermissionDto(row.id, row.name)

  implicit val encoder: Encoder[PermissionDto] = deriveEncoder
  implicit val decoder: Decoder[PermissionDto] = deriveDecoder
}
