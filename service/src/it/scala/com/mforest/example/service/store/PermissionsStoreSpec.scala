package com.mforest.example.service.store

import cats.effect.IO
import cats.implicits.none
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.ServiceSpec
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

final class PermissionsStoreSpec extends AsyncWordSpec with ServiceSpec with Matchers {

  private val dao: PermissionDao          = PermissionDao()
  private val store: PermissionsStore[IO] = PermissionsStore(dao, client, transactor)

  "PermissionsStore" when {

    "call get" must {

      "respond with none for not existing user" in {
        val id = FUUID.fuuid("2b5633c3-56c8-472c-b5d9-00721ae00c60")

        store.get(id).value.asserting {
          _ shouldBe none
        }
      }
    }
  }
}
