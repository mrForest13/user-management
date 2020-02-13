import sbt.{Def, _}
import sbt.Keys._

object Dependencies {

  private object Versions {
    val cats       = "2.0.0"
    val circe      = "0.12.2"
    val fuuid      = "0.2.0"
    val tapir      = "0.12.20"
    val tsecV      = "0.1.0"
    val http4s     = "0.20.15"
    val logback    = "1.2.3"
    val doobie     = "0.7.1"
    val webjars    = "3.22.0"
    val log4Cats   = "1.0.1"
    val scalaTest  = "3.1.0"
    val quicklens  = "1.4.12"
    val pureConfig = "0.12.2"
  }

  private val config: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig"             % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-http4s"      % Versions.pureConfig,
    "com.github.pureconfig" %% "pureconfig-circe"       % Versions.pureConfig
  )

  private val logging: Seq[ModuleID] = Seq(
    "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4Cats,
    "ch.qos.logback"    % "logback-classic" % Versions.logback,
    "ch.qos.logback"    % "logback-core"    % Versions.logback
  )

  private val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % Versions.cats
  )
  private val fuuid: Seq[ModuleID] = Seq(
    "io.chrisdavenport" %% "fuuid"        % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-doobie" % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-circe"  % Versions.fuuid,
    "io.chrisdavenport" %% "fuuid-http4s" % Versions.fuuid
  )

  private val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core"     % Versions.doobie,
    "org.tpolecat" %% "doobie-postgres" % Versions.doobie,
    "org.tpolecat" %% "doobie-hikari"   % Versions.doobie
  )

  private val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
    "org.http4s" %% "http4s-circe"        % Versions.http4s,
    "org.http4s" %% "http4s-dsl"          % Versions.http4s
  )

  private val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core"           % Versions.circe,
    "io.circe" %% "circe-generic"        % Versions.circe,
    "io.circe" %% "circe-parser"         % Versions.circe,
    "io.circe" %% "circe-generic-extras" % Versions.circe
  )

  private val tsecV: Seq[ModuleID] = Seq(
    "io.github.jmcardon" %% "tsec-common"   % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-password" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-http4s"   % Versions.tsecV
  )

  private val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-cats"               % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Versions.tapir
  )

  private val test: Seq[ModuleID] = Seq(
    "com.softwaremill.quicklens" %% "quicklens" % Versions.quicklens % Test,
    "org.scalatest"              %% "scalatest" % Versions.scalaTest % Test
  )

  val application: Seq[ModuleID] = test ++ logging
  val http: Seq[ModuleID]        = http4s ++ tapir ++ test ++ logging
  val service: Seq[ModuleID]     = tsecV ++ test ++ logging
  val db: Seq[ModuleID]          = doobie ++ test ++ logging
  val core: Seq[ModuleID]        = config ++ fuuid ++ cats ++ circe ++ test ++ logging

  implicit class ModuleSettings(modules: Seq[ModuleID]) {
    def asSettings: Seq[Def.Setting[_]] = Seq(libraryDependencies ++= modules)
  }
}
