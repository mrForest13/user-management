package com.mforest.example.http.form

import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator
import com.mforest.example.service.model.Permission
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class PermissionForm(name: String) {

  def toDto: Permission = {
    Permission(name)
  }
}

object PermissionForm {

  implicit val validator: Validator[PermissionForm] = {
    case form: PermissionForm if form.name.isEmpty =>
      ValidationError(s"Name cannot be empty!").invalid
    case form: PermissionForm if form.name.length > 100 =>
      ValidationError(s"Name cannot be longer than 100 characters!").invalid
    case form @ (_: PermissionForm) => form.valid
  }

  implicit val encoder: Encoder[PermissionForm] = deriveEncoder
  implicit val decoder: Decoder[PermissionForm] = deriveDecoder
}
