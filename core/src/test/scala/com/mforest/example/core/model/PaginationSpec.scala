package com.mforest.example.core.model

import cats.implicits.catsSyntaxValidatedId
import cats.syntax.OptionSyntax
import com.mforest.example.core.error.Error.ValidationError
import com.mforest.example.core.validation.validate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PaginationSpec extends AnyWordSpec with Matchers with OptionSyntax {

  "Pagination" when {

    "call validate" must {

      "respond with pagination for valid data" in {
        val size       = 0
        val page       = 0
        val pagination = new Pagination(size, page)

        validate(pagination) shouldBe Pagination(0, 0).valid
      }

      "respond with pagination with default size" in {
        val size       = none
        val page       = 0.some
        val pagination = Pagination(size, page)

        validate(pagination) shouldBe Pagination(Pagination.default.size, 0).valid
      }

      "respond with pagination with default page" in {
        val size       = 0.some
        val page       = none
        val pagination = Pagination(size, page)

        validate(pagination) shouldBe Pagination(0, Pagination.default.page).valid
      }

      "respond with pagination with default size and page" in {
        val size       = none
        val page       = none
        val pagination = Pagination(size, page)

        validate(pagination) shouldBe Pagination.default.valid
      }

      "respond with validation error for size less than 0" in {
        val size       = -1
        val page       = 0
        val pagination = new Pagination(size, page)

        validate(pagination) shouldBe ValidationError("Size cannot be less than 0!").invalid
      }

      "respond with validation error for page less than 0" in {
        val size       = 0
        val page       = -1
        val pagination = new Pagination(size, page)

        validate(pagination) shouldBe ValidationError("Page cannot be less than 0!").invalid
      }
    }
  }
}
