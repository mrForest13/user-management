import sbt.{Def, _}
import sbt.Keys._

object Dependencies {

  private object Versions {
    val cats       = "2.1.0"
    val circe      = "0.13.0"
    val fuuid      = "0.3.0"
    val tapir      = "0.12.20"
    val tsec       = "0.2.0"
    val jedis      = "3.2.0"
    val http4s     = "0.21.1"
    val logback    = "1.2.3"
    val doobie     = "0.8.8"
    val flyway     = "6.2.4"
    val log4Cats   = "1.0.1"
    val scalaTest  = "3.1.0"
    val quicklens  = "1.4.12"
    val scalaCache = "0.28.0"
    val pureConfig = "0.12.2"
  }

  private val config: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig-core"        % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-cats"        % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-circe"       % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-generic"     % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.pureConfig
  )

  private val logging: Seq[ModuleID] = Seq(
    "ch.qos.logback"    % "logback-core"    % Versions.logback,
    "ch.qos.logback"    % "logback-classic" % Versions.logback,
    "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4Cats
  )

  private val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"   % Versions.cats,
    "org.typelevel" %% "cats-effect" % Versions.cats
  )
  private val fuuid: Seq[ModuleID] = Seq(
    "io.chrisdavenport" %% "fuuid"        % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-circe"  % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-doobie" % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-http4s" % Versions.fuuid
  )

  private val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core"      % Versions.doobie,
    "org.tpolecat" %% "doobie-hikari"    % Versions.doobie,
    "org.tpolecat" %% "doobie-postgres"  % Versions.doobie,
    "org.tpolecat" %% "doobie-scalatest" % Versions.doobie % "test,it,e2e"
  )

  private val jedis: Seq[ModuleID] = Seq(
    "redis.clients" % "jedis" % Versions.jedis
  )

  private val cache: Seq[ModuleID] = Seq(
    "com.github.cb372" %% "scalacache-core"        % Versions.scalaCache,
    "com.github.cb372" %% "scalacache-redis"       % Versions.scalaCache,
    "com.github.cb372" %% "scalacache-circe"       % Versions.scalaCache,
    "com.github.cb372" %% "scalacache-cats-effect" % Versions.scalaCache
  )

  private val flyway: Seq[ModuleID] = Seq(
    "org.flywaydb" % "flyway-core" % Versions.flyway
  )

  private val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl"          % Versions.http4s,
    "org.http4s" %% "http4s-circe"        % Versions.http4s,
    "org.http4s" %% "http4s-blaze-server" % Versions.http4s
  )

  private val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core"           % Versions.circe,
    "io.circe" %% "circe-parser"         % Versions.circe,
    "io.circe" %% "circe-generic"        % Versions.circe,
    "io.circe" %% "circe-generic-extras" % Versions.circe
  )

  private val tsecV: Seq[ModuleID] = Seq(
    "io.github.jmcardon" %% "tsec-common"   % Versions.tsec,
    "io.github.jmcardon" %% "tsec-http4s"   % Versions.tsec,
    "io.github.jmcardon" %% "tsec-password" % Versions.tsec
  )

  private val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-cats"               % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir
  )

  private val test: Seq[ModuleID] = Seq(
    "org.scalatest"              %% "scalatest" % Versions.scalaTest % "test,it,e2e",
    "com.softwaremill.quicklens" %% "quicklens" % Versions.quicklens % "test,it,e2e"
  )

  val application: Seq[ModuleID] = test ++ logging
  val http: Seq[ModuleID]        = http4s ++ tapir ++ test ++ logging
  val service: Seq[ModuleID]     = tsecV ++ cache ++ test ++ logging
  val db: Seq[ModuleID]          = doobie ++ jedis ++ flyway ++ test ++ logging
  val core: Seq[ModuleID]        = config ++ fuuid ++ cats ++ circe ++ test ++ logging

  implicit class ModuleSettings(modules: Seq[ModuleID]) {
    def asSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies ++= modules)
  }
}
