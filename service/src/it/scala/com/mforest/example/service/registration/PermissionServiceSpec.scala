package com.mforest.example.service.registration

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.mforest.example.core.error.Error.ConflictError
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.ServiceSpec
import com.mforest.example.service.model.PermissionMock
import com.mforest.example.service.permission.PermissionService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class PermissionServiceSpec extends AsyncWordSpec with ServiceSpec with Matchers {

  private val dao: PermissionDao             = PermissionDao()
  private val service: PermissionService[IO] = PermissionService(dao, transactor)

  "PermissionService" when {

    "call add permission" must {

      "respond with inserted message" in {
        val permission = PermissionMock.gen()

        val message = s"The permission with name: ${permission.name} has been created!"

        service.addPermission(permission).value.asserting {
          _ shouldBe message.asRight
        }
      }

      "respond with conflict message" in {
        val permission = PermissionMock.gen()

        val message = s"The permission with name: ${permission.name} already exists!"

        val action = for {
          _      <- service.addPermission(permission)
          result <- service.addPermission(permission)
        } yield result

        action.value.asserting {
          _ shouldBe ConflictError(message).asLeft
        }
      }
    }
  }
}
