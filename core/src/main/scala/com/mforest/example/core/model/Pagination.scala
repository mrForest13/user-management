package com.mforest.example.core.model

import cats.Functor.ops.toAllFunctorOps
import cats.implicits.catsKernelStdAlgebraForUnit
import com.mforest.example.core.validation.{Validator, validate}

final case class Pagination(size: Int, page: Int) {

  def offset: Int = size * page
}

object Pagination {

  val default: Pagination = new Pagination(size = 10, page = 0)

  def apply(size: Option[Int], page: Option[Int]): Pagination = {
    new Pagination(size.getOrElse(default.size), page.getOrElse(default.page))
  }

  implicit val validator: Validator[Pagination] = { pagination =>
    validate(pagination.size < 0, msg = "Size cannot be less than 0!")
      .combine(validate(pagination.page < 0, msg = "Page cannot be less than 0!"))
      .as(pagination)
  }
}
