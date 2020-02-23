package com.mforest.example.core

import cats.data.Validated
import com.mforest.example.core.error.Error.ValidationError

package object validation {

  type ValidationResult[A] = Validated[ValidationError, A]

  type Validator[A] = A => ValidationResult[A]

  def validate[A](x: A)(implicit validator: Validator[A]): ValidationResult[A] = validator(x)
}
