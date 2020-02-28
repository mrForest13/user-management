package com.mforest.example.core

import cats.effect.IO
import cats.syntax.OptionSyntax
import com.mforest.example.core.config.app.AppConfig
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.core.config.db.DatabaseConfig
import com.mforest.example.core.config.http.HttpConfig
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationInt

class ConfigLoaderSpec extends AnyWordSpec with Matchers with OptionSyntax {

  "ConfigLoader" when {

    "call load" must {

      "respond with application config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.app shouldBe AppConfig(
          name = "User Management",
          description = "Example",
          banner = "Example"
        )
      }

      "respond with token config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.auth.token shouldBe TokenConfig(10.minutes, 2.minutes.some)
      }

      "respond with http config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.http shouldBe HttpConfig(host = "0.0.0.0", port = 1)
      }

      "respond with database config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.database shouldBe DatabaseConfig(
          host = "localhost",
          port = 1,
          schema = "user-management",
          user = "user-management",
          password = "user-management",
          driver = "org.postgresql.Driver",
          poolSize = 1,
          migrate = true,
          migrationTable = "migration_history"
        )
      }
    }
  }
}
