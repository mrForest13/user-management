package com.mforest.example.core

import cats.effect.IO
import cats.syntax.OptionSyntax
import com.mforest.example.core.config.app.AppConfig
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.core.config.db.{MigrationConfig, PostgresConfig, RedisConfig}
import com.mforest.example.core.config.http.HttpConfig
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationInt

final class ConfigLoaderSpec extends AnyWordSpec with Matchers with OptionSyntax {

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

      "respond with swagger config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.swagger shouldBe Map(
          "displayRequestDuration" -> Seq("true"),
          "operationsSorter"       -> Seq("alpha"),
          "docExpansion"           -> Seq("none"),
          "tagsSorter"             -> Seq("alpha"),
          "deepLinking"            -> Seq("true"),
          "filter"                 -> Seq("")
        )
      }

      "respond with postgres config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.database.postgres shouldBe PostgresConfig(
          host = "localhost",
          port = 1,
          schema = "user-management",
          user = "user-management",
          password = "user-management",
          driver = "org.postgresql.Driver",
          connectPoolSize = 1,
          maxConnectionsPoolSize = 1,
          maxConnectionsPoolName = "user-management",
          properties = Map.empty
        )
      }

      "respond with redis config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.database.redis shouldBe RedisConfig(
          host = "localhost",
          port = 1,
          password = "user-management"
        )
      }

      "respond with migration config" in {
        val config = ConfigLoader[IO].load
          .use(IO.pure)
          .unsafeRunSync()

        config.database.migration shouldBe MigrationConfig(
          migrate = true,
          migrationTable = "migration_history"
        )
      }
    }
  }
}
