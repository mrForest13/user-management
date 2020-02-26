package com.mforest.example.db.dao

import com.mforest.example.db.DatabaseSpec
import com.mforest.example.db.row.UserRowMock
import org.postgresql.util.PSQLException
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserDaoSpec extends AnyWordSpec with Matchers with DatabaseSpec with BeforeAndAfterEach {

  private val dao: UserDao = UserDao()

  override def beforeEach(): Unit = {
    sql"""TRUNCATE TABLE USERS, USERS_PERMISSIONS""".update.run
      .transact(transactor)
      .map(_ => ())
      .unsafeRunSync()
  }

  "UserDao" when {

    "call insert" must {

      "respond with one inserted row" in {
        val row = UserRowMock.gen

        val action = dao.insert(row)

        action.transact(transactor).unsafeRunSync() shouldBe 1
      }

      "throw exception on unique email field" in {
        val firstRow = UserRowMock.gen
        val secondRow = UserRowMock.gen

        val action = dao.insert(firstRow).flatMap(_ => dao.insert(secondRow))

        intercept[PSQLException] {
          action.transact(transactor).unsafeRunSync()
        }
      }
    }
  }
}
