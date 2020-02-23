package com.mforest.example.http.support

import cats.data.EitherT
import cats.effect.Sync
import com.mforest.example.core.error.Error
import com.mforest.example.core.validation.Validator

trait ValidationSupport {

  def validate[F[_]: Sync, T](obj: T)(implicit validator: Validator[T]): EitherT[F, Error, T] = {
    EitherT.fromEither[F](validator(obj).toEither)
  }
}
