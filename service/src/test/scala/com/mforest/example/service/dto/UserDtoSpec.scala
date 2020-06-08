package com.mforest.example.service.dto

import cats.data.{Chain, NonEmptyList}
import com.mforest.example.db.row.UserRow
import com.mforest.example.service.converter.DtoConverter.{ChainConverter, NonEmptyListConverter}
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class UserDtoSpec extends AnyWordSpec with Matchers {

  "UserRow" when {

    "chain to" must {

      "respond with user dto" in {
        val fuuid = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val row = Chain.one {
          UserRow(
            id = fuuid,
            email = "john.smith@gmail.com",
            hash = "hash",
            salt = fuuid,
            firstName = "john",
            lastName = "smith",
            city = "London",
            country = "England",
            phone = "123456789"
          )
        }
        val result = Chain.one {
          UserDto(
            id = fuuid,
            email = "john.smith@gmail.com",
            firstName = "john",
            lastName = "smith",
            city = "London",
            country = "England",
            phone = "123456789"
          )
        }

        row.to[UserDto] shouldBe result
      }
    }

    "non empty list to" must {

      "respond with user dto" in {
        val fuuid = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val row = NonEmptyList.one {
          UserRow(
            id = fuuid,
            email = "john.smith@gmail.com",
            hash = "hash",
            salt = fuuid,
            firstName = "john",
            lastName = "smith",
            city = "London",
            country = "England",
            phone = "123456789"
          )
        }
        val result = NonEmptyList.one {
          UserDto(
            id = fuuid,
            email = "john.smith@gmail.com",
            firstName = "john",
            lastName = "smith",
            city = "London",
            country = "England",
            phone = "123456789"
          )
        }
        row.to[UserDto] shouldBe result
      }
    }
  }
}
