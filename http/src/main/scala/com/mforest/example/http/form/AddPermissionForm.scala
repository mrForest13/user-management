package com.mforest.example.http.form

import cats.Functor.ops.toAllFunctorOps
import cats.implicits.catsKernelStdAlgebraForUnit
import com.mforest.example.core.validation.{Validator, validate}
import com.mforest.example.service.model.Permission
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import scala.util.matching.Regex

final case class AddPermissionForm(name: String) {

  def toDto: Permission = {
    Permission(name)
  }
}

object AddPermissionForm {

  private val nameRegex: Regex = "[A-Z]+(_[A-Z])*".r

  implicit val validator: Validator[AddPermissionForm] = { form =>
    validate(!nameRegex.matches(form.name), msg = "Wrong permission form. It should look like XXX_XXX!")
      .combine(validate(form.name.length > 100, msg = "Name cannot be longer than 100 characters!"))
      .combine(validate(form.name.isEmpty, msg = "Name cannot be empty!"))
      .as(form)
  }

  implicit val encoder: Encoder[AddPermissionForm] = deriveEncoder
  implicit val decoder: Decoder[AddPermissionForm] = deriveDecoder
}
