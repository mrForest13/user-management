package com.mforest.example.service

import cats.effect.IO
import com.mforest.example.service.hash.{HashEngine, SCryptEngine}
import io.chrisdavenport.fuuid.FUUID
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import tsec.common.{VerificationFailed, Verified}
import tsec.passwordhashers.jca.SCrypt

final class SCryptEngineSpec extends AnyWordSpec with Matchers {

  private val engine: HashEngine[IO, SCrypt] = SCryptEngine[IO]()

  "SCryptEngine" when {

    "checkPassword" must {

      "respond with verified status" in {
        val salt     = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val password = "example"

        val hash = "$s0$e0801$VmDwUokGt/g2dySZCvVXqQ==$s2hUj/UmVEVE0pOFWs2da0E4I+cqfcIuGSNPjJb6bfI="

        engine.checkPassword(password, salt, hash).unsafeRunSync() shouldBe Verified
      }

      "respond with Verification failed status" in {
        val salt     = FUUID.fuuid("8ea16e29-3978-4113-8a06-eca8228f78ff")
        val password = "example"

        val hash = "invalid"

        engine.checkPassword(password, salt, hash).unsafeRunSync() shouldBe VerificationFailed
      }
    }
  }
}
