package com.mforest.example.db.dao

import com.mforest.example.db.DatabaseSpec
import com.mforest.example.db.row.PermissionRow
import org.postgresql.util.PSQLException
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PermissionDaoSpec extends AnyWordSpec with Matchers with DatabaseSpec with BeforeAndAfterEach {

  private val dao: PermissionDao = PermissionDao()

  override def beforeEach(): Unit = {
    sql"""TRUNCATE TABLE PERMISSIONS, USERS_PERMISSIONS""".update.run
      .transact(transactor)
      .map(_ => ())
      .unsafeRunSync()
  }

  "PermissionDao" when {

    "call insert" must {

      "respond with one inserted row" in {
        val name = "EXAMPLE_PERMISSION"
        val row  = PermissionRow(randomUnsafeId, name)

        val action = dao.insert(row)

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }

      "throw exception on unique field" in {
        val name      = "EXAMPLE_PERMISSION"
        val firstRow  = PermissionRow(randomUnsafeId, name)
        val secondRow = PermissionRow(randomUnsafeId, name)

        val action = dao.insert(firstRow).flatMap(_ => dao.insert(secondRow))

        intercept[PSQLException] {
          action.transact(transactor).unsafeRunSync()
        }
      }
    }

    "call delete" must {

      "respond with zero deleted row" in {
        val id = randomUnsafeId

        val action = dao.delete(id)

        action.transact(transactor).unsafeRunSync() shouldBe 0
      }

      "respond with one deleted row" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRow(randomUnsafeId, firstName)
        val secondRow  = PermissionRow(randomUnsafeId, secondName)

        val action = for {
          _     <- dao.insert(firstRow)
          _     <- dao.insert(secondRow)
          count <- dao.delete(firstRow.id)
        } yield count

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }
    }

    "find by id" must {

      "respond with none" in {
        val id = randomUnsafeId

        val action = dao.find(id)

        action.transact(transactor).value.unsafeRunSync() shouldBe none
      }

      "respond with one record" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRow(randomUnsafeId, firstName)
        val secondRow  = PermissionRow(randomUnsafeId, secondName)

        val action = for {
          _      <- dao.insert(firstRow)
          _      <- dao.insert(secondRow)
          record <- dao.find(firstRow.id).value
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe firstRow.some
      }
    }

    "find by name" must {

      "respond with none" in {
        val id = randomUnsafeId

        val action = dao.find(id)

        action.transact(transactor).value.unsafeRunSync() shouldBe none
      }

      "respond with one record" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRow(randomUnsafeId, firstName)
        val secondRow  = PermissionRow(randomUnsafeId, secondName)

        val action = for {
          _      <- dao.insert(firstRow)
          _      <- dao.insert(secondRow)
          record <- dao.find(firstName).value
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe firstRow.some
      }
    }
  }
}
