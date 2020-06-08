package com.mforest.example.service.dto

import cats.data.{Chain, NonEmptyList}
import com.mforest.example.db.row.PermissionRow
import com.mforest.example.service.converter.DtoConverter.{ChainConverter, NonEmptyListConverter}
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class PermissionDtoSpec extends AnyWordSpec with Matchers {

  "PermissionRow" when {

    "chain to" must {

      "respond with permission dto" in {
        val name   = "Example"
        val id     = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val row    = Chain.one(PermissionRow(id, name))
        val result = Chain.one(PermissionDto(id, name))

        row.to[PermissionDto] shouldBe result
      }
    }

    "non empty list to" must {

      "respond with permission dto" in {
        val name   = "Example"
        val id     = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val row    = NonEmptyList.one(PermissionRow(id, name))
        val result = NonEmptyList.one(PermissionDto(id, name))

        row.to[PermissionDto] shouldBe result
      }
    }
  }
}
