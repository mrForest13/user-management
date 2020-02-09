package com.mforest.example.core

import cats.Monad
import cats.data.Validated.{Invalid, Valid}
import cats.data.Validated
import com.mforest.example.core.error.Error.ValidationError

package object validation {

  type ValidationResult[A] = Validated[ValidationError, A]

  type Validator[A] = A => ValidationResult[A]

  def validate[A](x: A)(implicit validator: Validator[A]): ValidationResult[A] = validator(x)

  implicit def validatedMonad: Monad[ValidationResult] = new Monad[ValidationResult] {

    override def pure[A](x: A): ValidationResult[A] = Valid(x)

    override def flatMap[A, B](fa: ValidationResult[A])(f: A => ValidationResult[B]): ValidationResult[B] = {
      fa match {
        case Valid(a)       => f(a)
        case i @ Invalid(_) => i
      }
    }

    @scala.annotation.tailrec
    override def tailRecM[A, B](a: A)(f: A => ValidationResult[Either[A, B]]): ValidationResult[B] = {
      f(a) match {
        case Valid(Right(b)) => Valid(b)
        case Valid(Left(a))  => tailRecM(a)(f)
        case i @ Invalid(_)  => i
      }
    }
  }
}
