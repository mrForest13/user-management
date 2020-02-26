package com.mforest.example.db.dao

import cats.data.Chain
import cats.effect.IO
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.DatabaseSpec
import com.mforest.example.db.row.PermissionRowMock
import io.chrisdavenport.fuuid.FUUID
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
        val row  = PermissionRowMock.gen

        val action = dao.insert(row)

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }

      "throw exception on unique name field" in {
        val firstRow  = PermissionRowMock.gen
        val secondRow = PermissionRowMock.gen

        val action = dao.insert(firstRow).flatMap(_ => dao.insert(secondRow))

        intercept[PSQLException] {
          action.transact(transactor).unsafeRunSync()
        }
      }
    }

    "call delete" must {

      "respond with zero deleted row" in {
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = dao.delete(id)

        action.transact(transactor).unsafeRunSync() shouldBe 0
      }

      "respond with one deleted row" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

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
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = dao.find(id)

        action.transact(transactor).value.unsafeRunSync() shouldBe none
      }

      "respond with one record" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

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
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = dao.find(id)

        action.transact(transactor).value.unsafeRunSync() shouldBe none
      }

      "respond with one record" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- dao.insert(firstRow)
          _      <- dao.insert(secondRow)
          record <- dao.find(firstName).value
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe firstRow.some
      }
    }

    "find with pagination" must {

      "respond with empty chain" in {
        val pagination = Pagination.default

        val action = dao.find(pagination)

        action.transact(transactor).unsafeRunSync() shouldBe Chain.empty
      }

      "respond with one record" in {
        val pagination= new Pagination(size = 1, page = 0)
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- dao.insert(firstRow)
          _      <- dao.insert(secondRow)
          record <- dao.find(pagination)
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe Chain(firstRow)
      }

      "respond with all records" in {
        val pagination = Pagination.default
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- dao.insert(firstRow)
          _      <- dao.insert(secondRow)
          record <- dao.find(pagination)
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe Chain(firstRow, secondRow)
      }
    }
  }
}
