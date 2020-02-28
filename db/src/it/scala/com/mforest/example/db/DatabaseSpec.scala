package com.mforest.example.db

import cats.effect.{Blocker, ContextShift, IO}
import cats.syntax.OptionSyntax
import com.mforest.example.core.config.Config
import com.mforest.example.core.config.db.DatabaseConfig
import com.mforest.example.db.migration.MigrationManager
import doobie.scalatest.IOChecker
import doobie.syntax.{AllSyntax, ToConnectionIOOps}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.scalatest.wordspec.AnyWordSpec
import pureconfig.generic.auto.exportReader
import pureconfig.module.catseffect.loadConfigF

trait DatabaseSpec extends AnyWordSpec with IOChecker with ToConnectionIOOps with AllSyntax with OptionSyntax {

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  override val transactor: Transactor[IO] = {
    (for {
      config     <- loadConfigF[IO, Config]
      blocker    = Blocker.liftExecutionContext(ExecutionContexts.synchronous)
      transactor = testTransactor(config.database, blocker)
      _          <- MigrationManager[IO](config.database).migrate()
    } yield transactor).unsafeRunSync()
  }

  private def testTransactor(config: DatabaseConfig, blocker: Blocker): Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      driver = config.driver,
      url = config.postgresUrl,
      user = config.user,
      pass = config.password,
      blocker = blocker
    )
  }
}
