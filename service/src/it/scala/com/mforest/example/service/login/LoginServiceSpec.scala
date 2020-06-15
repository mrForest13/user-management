package com.mforest.example.service.login

import cats.data.EitherT
import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.mforest.example.core.error.Error.UnauthorizedError
import com.mforest.example.db.dao.UserDao
import com.mforest.example.db.row.UserRow
import com.mforest.example.service.ServiceSpec
import com.mforest.example.service.hash.{HashEngine, SCryptEngine}
import com.mforest.example.service.model.Credentials
import io.chrisdavenport.fuuid.FUUID
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import tsec.passwordhashers.jca.SCrypt

final class LoginServiceSpec extends AsyncWordSpec with ServiceSpec with AsyncMockFactory with Matchers {

  private val dao: UserDao                   = UserDao()
  private val engine: HashEngine[IO, SCrypt] = SCryptEngine[IO]()
  private val service: LoginService[IO]      = LoginService(dao, engine, transactor)

  "LoginService" when {

    "call login" must {

      "respond with user id" in {
        val id          = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val email       = "john.smith@gmail.com"
        val password    = "example"
        val credentials = Credentials(email, password)

        val action = for {
          _      <- EitherT.right(insertUser(id))
          result <- service.login(credentials)
        } yield result

        action.value.asserting {
          _ shouldBe id.asRight
        }
      }

      "respond with unauthorized error on wrong password" in {
        val id          = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val email       = "john.smith@gmail.com"
        val password    = "example1"
        val credentials = Credentials(email, password)

        val message = "Wrong email or password!"

        val action = for {
          _      <- EitherT.right(insertUser(id))
          result <- service.login(credentials)
        } yield result

        action.value.asserting {
          _ shouldBe UnauthorizedError(message).asLeft
        }
      }

      "respond with unauthorized error when user not exists" in {
        val email       = "john.smith@gmail.com"
        val password    = "example"
        val credentials = Credentials(email, password)

        val message = "Wrong email or password!"

        service.login(credentials).value.asserting {
          _ shouldBe UnauthorizedError(message).asLeft
        }
      }

      def insertUser(id: FUUID): IO[Int] = {
        val row = UserRow(
          id = id,
          email = "john.smith@gmail.com",
          hash = "$s0$e0801$VmDwUokGt/g2dySZCvVXqQ==$s2hUj/UmVEVE0pOFWs2da0E4I+cqfcIuGSNPjJb6bfI=",
          salt = id,
          firstName = "john",
          lastName = "smith",
          city = "London",
          country = "England",
          phone = "123456789"
        )

        dao.insert(row).transact(transactor)
      }
    }
  }
}
