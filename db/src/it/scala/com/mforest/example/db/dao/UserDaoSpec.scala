package com.mforest.example.db.dao

import cats.data.Chain
import cats.effect.IO
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.DatabaseSpec
import com.mforest.example.db.row.{PermissionRowMock, UserRowMock}
import io.chrisdavenport.fuuid.FUUID
import org.postgresql.util.PSQLException
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class UserDaoSpec extends AnyWordSpec with Matchers with DatabaseSpec with BeforeAndAfterEach {

  private val userDao: UserDao             = UserDao()
  private val permissionDao: PermissionDao = PermissionDao()

  override def beforeEach(): Unit = {
    sql"""TRUNCATE TABLE USERS, PERMISSIONS, USERS_PERMISSIONS""".update.run
      .transact(transactor)
      .map(_ => ())
      .unsafeRunSync()
  }

  "UserDao" when {

    "call insert" must {

      "respond with one inserted row" in {
        val row = UserRowMock.gen()

        val action = userDao.insert(row)

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }

      "throw exception on unique email field" in {
        val firstRow  = UserRowMock.gen()
        val secondRow = UserRowMock.gen()

        val action = for {
          _ <- userDao.insert(firstRow)
          _ <- userDao.insert(secondRow)
        } yield ()

        intercept[PSQLException] {
          action.transact(transactor).unsafeRunSync()
        }
      }
    }

    "call delete" must {

      "respond with zero deleted row" in {
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = userDao.delete(id)

        action.transact(transactor).unsafeRunSync() shouldBe 0
      }

      "respond with one deleted row" in {
        val firstEmail  = "FIRST_EXAMPLE_EMAIL"
        val secondEmail = "SECOND_EXAMPLE_EMAIL"
        val firstRow    = UserRowMock.gen(firstEmail)
        val secondRow   = UserRowMock.gen(secondEmail)

        val action = for {
          _     <- userDao.insert(firstRow)
          _     <- userDao.insert(secondRow)
          count <- userDao.delete(firstRow.id)
        } yield count

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }
    }

    "find by id" must {

      "respond with none" in {
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = permissionDao.find(id)

        action.transact(transactor).value.unsafeRunSync() shouldBe none
      }

      "respond with one record" in {
        val firstEmail  = "FIRST_EXAMPLE_EMAIL"
        val secondEmail = "SECOND_EXAMPLE_EMAIL"
        val firstRow    = UserRowMock.gen(firstEmail)
        val secondRow   = UserRowMock.gen(secondEmail)

        val action = for {
          _      <- userDao.insert(firstRow)
          _      <- userDao.insert(secondRow)
          record <- userDao.find(firstRow.id).value
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe firstRow.some
      }
    }

    "find by email" must {

      "respond with none" in {
        val email = "FIRST_EXAMPLE_EMAIL"

        val action = userDao.find(email)

        action.transact(transactor).value.unsafeRunSync() shouldBe none
      }

      "respond with one record" in {
        val firstEmail  = "FIRST_EXAMPLE_EMAIL"
        val secondEmail = "SECOND_EXAMPLE_EMAIL"
        val firstRow    = UserRowMock.gen(firstEmail)
        val secondRow   = UserRowMock.gen(secondEmail)

        val action = for {
          _      <- userDao.insert(firstRow)
          _      <- userDao.insert(secondRow)
          record <- userDao.find(firstEmail).value
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe firstRow.some
      }
    }

    "find with pagination" must {

      "respond with empty chain" in {
        val pagination = Pagination.default

        val action = userDao.find(pagination)

        action.transact(transactor).unsafeRunSync() shouldBe Chain.empty
      }

      "respond with one record" in {
        val pagination  = new Pagination(size = 1, page = 0)
        val firstEmail  = "FIRST_EXAMPLE_EMAIL"
        val secondEmail = "SECOND_EXAMPLE_EMAIL"
        val firstRow    = UserRowMock.gen(firstEmail)
        val secondRow   = UserRowMock.gen(secondEmail)

        val action = for {
          _      <- userDao.insert(firstRow)
          _      <- userDao.insert(secondRow)
          record <- userDao.find(pagination)
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe Chain(firstRow)
      }

      "respond with all records" in {
        val pagination  = Pagination.default
        val firstEmail  = "FIRST_EXAMPLE_EMAIL"
        val secondEmail = "SECOND_EXAMPLE_EMAIL"
        val firstRow    = UserRowMock.gen(firstEmail)
        val secondRow   = UserRowMock.gen(secondEmail)

        val action = for {
          _      <- userDao.insert(firstRow)
          _      <- userDao.insert(secondRow)
          record <- userDao.find(pagination)
        } yield record

        action.transact(transactor).unsafeRunSync() shouldBe Chain(firstRow, secondRow)
      }
    }

    "add permission for user" must {

      "throw exception for nonexistent user and permission" in {
        val userId       = FUUID.randomFUUID[IO].unsafeRunSync()
        val permissionID = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = userDao.add(userId, permissionID)

        intercept[PSQLException] {
          action.transact(transactor).unsafeRunSync()
        }
      }

      "respond with one inserted row" in {
        val userRow       = UserRowMock.gen()
        val permissionRow = PermissionRowMock.gen()

        val action = for {
          _     <- userDao.insert(userRow)
          _     <- permissionDao.insert(permissionRow)
          count <- userDao.add(userRow.id, permissionRow.id)
        } yield count

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }

      "do nothing on conflict" in {
        val userRow       = UserRowMock.gen()
        val permissionRow = PermissionRowMock.gen()

        val action = for {
          _     <- userDao.insert(userRow)
          _     <- permissionDao.insert(permissionRow)
          _     <- userDao.add(userRow.id, permissionRow.id)
          count <- userDao.add(userRow.id, permissionRow.id)
        } yield count

        action.transact(transactor).unsafeRunSync() shouldBe 0
      }
    }

    "delete permission for user" must {

      "respond with zero deleted row" in {
        val userId       = FUUID.randomFUUID[IO].unsafeRunSync()
        val permissionID = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = userDao.delete(userId, permissionID)

        action.transact(transactor).unsafeRunSync() shouldBe 0
      }

      "respond with one deleted row" in {
        val userRow       = UserRowMock.gen()
        val permissionRow = PermissionRowMock.gen()

        val action = for {
          _     <- userDao.insert(userRow)
          _     <- permissionDao.insert(permissionRow)
          _     <- userDao.add(userRow.id, permissionRow.id)
          count <- userDao.delete(userRow.id, permissionRow.id)
        } yield count

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }
    }
  }
}
