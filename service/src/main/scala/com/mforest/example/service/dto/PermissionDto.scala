package com.mforest.example.service.dto

import cats.Id
import com.mforest.example.core.formatter.FuuidFormatter
import com.mforest.example.db.row.PermissionRow
import com.mforest.example.service.converter.DtoConverter.Converter
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class PermissionDto(id: Id[FUUID], name: String)

object PermissionDto extends FuuidFormatter {

  implicit val converter: Converter[PermissionRow, PermissionDto] = { row =>
    PermissionDto(row.id, row.name)
  }

  implicit val encoder: Encoder[PermissionDto] = deriveEncoder
  implicit val decoder: Decoder[PermissionDto] = deriveDecoder
}
