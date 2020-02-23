package com.mforest.example.http.form

import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator
import com.mforest.example.service.model.Permission
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class AddPermissionForm(name: String) {

  def toDto: Permission = {
    Permission(name)
  }
}

object AddPermissionForm {

  implicit val validator: Validator[AddPermissionForm] = {
    case form: AddPermissionForm if form.name.isEmpty =>
      ValidationError(s"Name cannot be empty!").invalid
    case form: AddPermissionForm if form.name.length > 100 =>
      ValidationError(s"Name cannot be longer than 100 characters!").invalid
    case form @ (_: AddPermissionForm) => form.valid
  }

  implicit val encoder: Encoder[AddPermissionForm] = deriveEncoder
  implicit val decoder: Decoder[AddPermissionForm] = deriveDecoder
}
