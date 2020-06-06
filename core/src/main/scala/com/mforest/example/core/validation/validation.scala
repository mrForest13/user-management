package com.mforest.example.core

import cats.data.ValidatedNel
import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError

package object validation {

  type ValidationResult[A] = ValidatedNel[ValidationError, A]

  type Validator[A] = A => ValidationResult[A]

  def validate(condition: Boolean, msg: String): ValidatedNel[ValidationError, Unit] = {
    if (condition) ValidationError(msg).invalidNel else ().validNel
  }

  def validate[A](x: A)(implicit validator: Validator[A]): ValidationResult[A] = validator(x)
}
