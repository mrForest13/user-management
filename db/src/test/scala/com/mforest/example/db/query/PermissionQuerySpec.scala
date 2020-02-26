package com.mforest.example.db.query

import com.mforest.example.db.row.PermissionRow
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PermissionQuerySpec extends AnyWordSpec with Matchers {

  private val query = new PermissionQuery

  "PermissionQuery" when {

    "insert" must {

      "respond with valid sql string" in {
        val id   = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val name = "EXAMPLE_PERMISSION"
        val row  = PermissionRow(id, name)

        val sql = "INSERT INTO PERMISSIONS (ID, NAME) VALUES (?, ?)"

        query.insert(row).sql.trim shouldBe sql
      }
    }

    "delete" must {

      "respond with valid sql string" in {
        val id = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")

        val sql = "DELETE FROM PERMISSIONS WHERE ID = ?"

        query.delete(id).sql.trim shouldBe sql
      }
    }

    "select by id" must {

      "respond with valid sql string" in {
        val id = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")

        val sql = "SELECT ID, NAME FROM PERMISSIONS WHERE ID = ?"

        query.select(id).sql.trim shouldBe sql
      }
    }

    "select by name" must {

      "respond with valid sql string" in {
        val name = "EXAMPLE_PERMISSION"

        val sql = "SELECT ID, NAME FROM PERMISSIONS WHERE NAME = ?"

        query.select(name).sql.trim shouldBe sql
      }
    }

//    "select with pagination" must {
//
//      "respond with valid sql string" in {
//        val pagination = Pagination.default
//
//        val sql = "SELECT ID, NAME FROM PERMISSIONS ORDER BY CREATED_AT ASC LIMIT ? OFFSET ?"
//
//        query.select(pagination).sql.trim shouldBe sql
//      }
//    }
  }
}
