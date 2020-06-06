package com.mforest.example.db.dao

import cats.data.Chain
import cats.effect.IO
import cats.implicits.{catsSyntaxOptionId, none}
import com.mforest.example.core.model.Pagination
import com.mforest.example.db.DatabaseSpec
import com.mforest.example.db.row.{PermissionRowMock, UserRowMock}
import io.chrisdavenport.fuuid.FUUID
import org.postgresql.util.PSQLException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class PermissionDaoSpec extends AsyncWordSpec with DatabaseSpec with Matchers {

  private val userDao: UserDao             = UserDao()
  private val permissionDao: PermissionDao = PermissionDao()

  "PermissionDao" when {

    "call insert" must {

      "respond with one inserted row" in {
        val row = PermissionRowMock.gen()

        val action = permissionDao.insert(row)

        action.transact(transactor).asserting {
          _ shouldBe 1
        }
      }

      "throw exception on unique name field" in {
        val firstRow  = PermissionRowMock.gen()
        val secondRow = PermissionRowMock.gen()

        val action = for {
          _ <- permissionDao.insert(firstRow)
          _ <- permissionDao.insert(secondRow)
        } yield ()

        action.transact(transactor).assertThrows[PSQLException]
      }
    }

    "call delete" must {

      "respond with zero deleted row" in {
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = permissionDao.delete(id)

        action.transact(transactor).asserting {
          _ shouldBe 0
        }
      }

      "respond with one deleted row" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _     <- permissionDao.insert(firstRow)
          _     <- permissionDao.insert(secondRow)
          count <- permissionDao.delete(firstRow.id)
        } yield count

        action.transact(transactor).asserting {
          _ shouldBe 1
        }
      }
    }

    "find by id" must {

      "respond with none" in {
        val id = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = permissionDao.find(id).value

        action.transact(transactor).asserting {
          _ shouldBe none
        }
      }

      "respond with one record" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- permissionDao.insert(firstRow)
          _      <- permissionDao.insert(secondRow)
          record <- permissionDao.find(firstRow.id).value
        } yield record

        action.transact(transactor).asserting {
          _ shouldBe firstRow.some
        }
      }
    }

    "find by name" must {

      "respond with none" in {
        val name = "FIRST_EXAMPLE_PERMISSION"

        val action = permissionDao.find(name).value

        action.transact(transactor).asserting {
          _ shouldBe none
        }
      }

      "respond with one record" in {
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- permissionDao.insert(firstRow)
          _      <- permissionDao.insert(secondRow)
          record <- permissionDao.find(firstName).value
        } yield record

        action.transact(transactor).asserting {
          _ shouldBe firstRow.some
        }
      }
    }

    "find with pagination" must {

      "respond with empty chain" in {
        val pagination = Pagination.default

        val action = permissionDao.find(pagination)

        action.transact(transactor).asserting(_ shouldBe Chain.empty)
      }

      "respond with one record" in {
        val pagination = new Pagination(size = 1, page = 0)
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- permissionDao.insert(firstRow)
          _      <- permissionDao.insert(secondRow)
          record <- permissionDao.find(pagination)
        } yield record

        action.transact(transactor).asserting {
          _ shouldBe Chain(firstRow)
        }
      }

      "respond with all records" in {
        val pagination = Pagination.default
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _      <- permissionDao.insert(firstRow)
          _      <- permissionDao.insert(secondRow)
          record <- permissionDao.find(pagination)
        } yield record

        action.transact(transactor).asserting {
          _ shouldBe Chain(firstRow, secondRow)
        }
      }
    }

    "find by user id" must {

      "respond with empty chain" in {
        val userId = FUUID.randomFUUID[IO].unsafeRunSync()

        val action = permissionDao.findByUser(userId)

        action.transact(transactor).asserting {
          _ shouldBe Chain.empty
        }
      }

      "respond with user permissions" in {
        val userRow    = UserRowMock.gen()
        val firstName  = "FIRST_EXAMPLE_PERMISSION"
        val secondName = "SECOND_EXAMPLE_PERMISSION"
        val firstRow   = PermissionRowMock.gen(firstName)
        val secondRow  = PermissionRowMock.gen(secondName)

        val action = for {
          _       <- userDao.insert(userRow)
          _       <- permissionDao.insert(firstRow)
          _       <- permissionDao.insert(secondRow)
          _       <- userDao.add(userRow.id, firstRow.id)
          _       <- userDao.add(userRow.id, secondRow.id)
          records <- permissionDao.findByUser(userRow.id)
        } yield records

        action.transact(transactor).asserting {
          _ shouldBe Chain(firstRow, secondRow)
        }
      }
    }
  }
}
