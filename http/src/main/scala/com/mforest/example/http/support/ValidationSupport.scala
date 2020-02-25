package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.Sync
import cats.kernel.Semigroup
import com.mforest.example.core.error.Error
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator

trait ValidationSupport {

  private implicit val semigroup: Semigroup[ValidationError] = (x: ValidationError, y: ValidationError) => {
    ValidationError(x.reason concat System.lineSeparator() concat y.reason)
  }

  def validate[F[_]: Sync, T](obj: T)(implicit validator: Validator[T]): EitherT[F, Error, T] = {
    EitherT
      .fromEither[F](validator(obj).toEither)
      .leftMap(_.reduceLeft(Semigroup[ValidationError].combine))
  }
}
