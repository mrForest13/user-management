package com.mforest.example.db

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Blocker, IO}
import com.mforest.example.core.config.Config
import com.mforest.example.core.config.db.PostgresConfig
import com.mforest.example.db.migration.MigrationManager
import doobie.scalatest.IOChecker
import doobie.syntax.ToConnectionIOOps
import doobie.syntax.string.toSqlInterpolator
import doobie.util.transactor.Transactor
import org.scalatest.{AsyncTestSuite, BeforeAndAfterAll, BeforeAndAfterEach}
import pureconfig.generic.auto.exportReader
import pureconfig.module.catseffect.loadConfigF

trait DatabaseSpec
    extends IOChecker
    with AsyncIOSpec
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with ToConnectionIOOps {
  this: AsyncTestSuite =>

  override def beforeEach(): Unit = truncateAll()

  override def afterAll(): Unit = dropAll()

  override val transactor: Transactor[IO] = {
    (for {
      config     <- loadConfigF[IO, Config]
      blocker    = Blocker.liftExecutionContext(executionContext)
      transactor = testTransactor(config.database.postgres, blocker)
      _          = MigrationManager[IO](config.database).migrate()
    } yield transactor).unsafeRunSync()
  }

  private def testTransactor(config: PostgresConfig, blocker: Blocker): Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      driver = config.driver,
      url = config.postgresUrl,
      user = config.user,
      pass = config.password,
      blocker = blocker
    )
  }

  private def dropAll(): Unit = {
    val action = for {
      _ <- sql"DROP TABLE USERS_PERMISSIONS".update.run
      _ <- sql"DROP TABLE PERMISSIONS".update.run
      _ <- sql"DROP TABLE USERS".update.run
      _ <- sql"DROP TABLE MIGRATION_HISTORY".update.run
    } yield ()

    action
      .transact(transactor)
      .unsafeRunSync()
  }

  def truncateAll(): Unit = {
    sql"""TRUNCATE TABLE USERS, PERMISSIONS, USERS_PERMISSIONS""".update.run
      .transact(transactor)
      .as(())
      .unsafeRunSync()
  }
}
