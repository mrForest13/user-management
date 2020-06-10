package com.mforest.example.service.registration

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.mforest.example.core.error.Error.ConflictError
import com.mforest.example.db.dao.UserDao
import com.mforest.example.service.ServiceSpec
import com.mforest.example.service.hash.{HashEngine, SCryptEngine}
import com.mforest.example.service.model.UserMock
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.passwordhashers.jca.SCrypt

final class RegistrationServiceSpec extends AsyncWordSpec with ServiceSpec with AsyncMockFactory with Matchers {

  private val dao: UserDao                     = UserDao()
  private val engine: HashEngine[IO, SCrypt]   = SCryptEngine[IO]()
  private val service: RegistrationService[IO] = RegistrationService(dao, engine, transactor)

  "RegistrationService" when {

    "call register" must {

      "respond with inserted message" in {
        val user = UserMock.gen()

        val message = s"The user with email ${user.email} has been created."

        service.register(user).value.asserting {
          _ shouldBe message.asRight
        }
      }

      "respond with conflict message" in {
        val user = UserMock.gen()

        val message = s"The user with email ${user.email} already exists!"

        val action = for {
          _      <- service.register(user)
          result <- service.register(user)
        } yield result

        action.value.asserting {
          _ shouldBe ConflictError(message).asLeft
        }
      }
    }
  }
}
