package com.mforest.example.service.dto

import cats.Id
import com.mforest.example.core.formatter.FuuidFormatter
import com.mforest.example.db.row.UserRow
import com.mforest.example.service.converter.DtoConverter.Converter
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class UserDto(
    id: Id[FUUID],
    email: String,
    firstName: String,
    lastName: String,
    city: String,
    country: String,
    phone: String
)

object UserDto extends FuuidFormatter {

  implicit val converter: Converter[UserRow, UserDto] = { row =>
    UserDto(row.id, row.email, row.firstName, row.lastName, row.city, row.country, row.phone)
  }

  implicit val encoder: Encoder[UserDto] = deriveEncoder
  implicit val decoder: Decoder[UserDto] = deriveDecoder
}
