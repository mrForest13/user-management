package com.mforest.example.http.validation

import cats.data.EitherT
import cats.effect.Sync
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator

trait ValidationSupport {

  def validate[F[_]: Sync, T](obj: T)(implicit validator: Validator[T]): EitherT[F, ValidationError, T] = {
    EitherT.fromEither[F](validator(obj).toEither)
  }
}
