package com.mforest.example.core.model

import cats.implicits.catsSyntaxValidatedId
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.Validator

final case class Pagination(size: Int, page: Int) {

  def offset: Int = size * page
}

object Pagination {

  val default: Pagination = new Pagination(size = 10, page = 0)

  def apply(size: Option[Int], page: Option[Int]): Pagination = {
    new Pagination(size.getOrElse(default.size), page.getOrElse(default.page))
  }

  implicit val validator: Validator[Pagination] = {
    case pagination: Pagination if pagination.size < 0 =>
      ValidationError("Size cannot be less than 0!").invalid
    case pagination: Pagination if pagination.page < 0 =>
      ValidationError("Page cannot be less than 0!").invalid
    case pagination @ (_: Pagination) => pagination.valid
  }
}
