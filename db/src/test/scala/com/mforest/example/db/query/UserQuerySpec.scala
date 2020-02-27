package com.mforest.example.db.query

import com.mforest.example.core.model.Pagination
import com.mforest.example.db.row.UserRow
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class UserQuerySpec extends AnyWordSpec with Matchers {

  private val query = new UserQuery

  "UserQuery" when {

    "insert" must {

      "respond with valid sql string" in {
        val fuuid = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val row = UserRow(
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

        val sql = """INSERT INTO USERS (ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME,
          CITY, COUNTRY, PHONE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""".normalize

        query.insert(row).sql.normalize shouldBe sql
      }
    }

    "delete" must {

      "respond with valid sql string" in {
        val id = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")

        val sql = "DELETE FROM USERS WHERE ID = ?"

        query.delete(id).sql.normalize shouldBe sql
      }
    }

    "select by id" must {

      "respond with valid sql string" in {
        val id = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")

        val sql = """SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME,
          CITY, COUNTRY, PHONE FROM USERS WHERE ID = ?""".normalize

        query.select(id).sql.normalize shouldBe sql
      }
    }

    "select with pagination" must {

      "respond with valid sql string" in {
        val pagination = Pagination.default

        val sql = """SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME,
          CITY, COUNTRY, PHONE FROM USERS ORDER BY CREATED_AT ASC
          LIMIT ? OFFSET ?""".normalize

        query.select(pagination).sql.normalize shouldBe sql
      }
    }

    "select by email" must {

      "respond with valid sql string" in {
        val email = "john.smith@gmail.com"

        val sql = """SELECT ID, EMAIL, HASH, SALT, FIRST_NAME, LAST_NAME,
          CITY, COUNTRY, PHONE FROM USERS WHERE EMAIL = ?""".normalize

        query.select(email).sql.normalize shouldBe sql
      }
    }

    "insert permission for user" must {

      "respond with valid sql string" in {
        val userId       = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val permissionId = FUUID.fuuid("1b4d6c6e-8f01-4a85-9da1-243431a4376d")

        val sql = """INSERT INTO USERS_PERMISSIONS (USER_ID, PERMISSION_ID)
          VALUES (?, ?)  ON CONFLICT DO NOTHING""".normalize

        query.add(userId, permissionId).sql.normalize shouldBe sql
      }
    }

    "delete permission for user" must {

      "respond with valid sql string" in {
        val userId       = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val permissionId = FUUID.fuuid("1b4d6c6e-8f01-4a85-9da1-243431a4376d")

        val sql = "DELETE FROM USERS_PERMISSIONS WHERE USER_ID = ? AND PERMISSION_ID = ?"

        query.delete(userId, permissionId).sql.normalize shouldBe sql
      }
    }
  }

  private implicit class QueryNormalizer(query: String) {

    def normalize: String = query.trim.replaceAll("\\s+", " ")
  }
}
