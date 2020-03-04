package com.mforest.example.core.permission

import cats.implicits.toShow
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PermissionsSpec extends AnyWordSpec with Matchers {

  "Permissions" when {

    "values" must {

      "each have prefix with module name" in {
        val modulePrefix = "USER_MANAGEMENT"
        val permissions  = Permissions.values.map(_.show)

        permissions.forall(_.startsWith(modulePrefix)) shouldBe true
      }
    }
  }
}
