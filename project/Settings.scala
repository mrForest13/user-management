import Dependencies.ModuleSettings
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{Docker, dockerExposedPorts, dockerUsername}
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import org.scalastyle.sbt.ScalastylePlugin.autoImport.{scalastyleFailOnError, scalastyleFailOnWarning}
import sbt.{Def, Tags, Global}
import sbt.Keys.{organization, scalaVersion, version, _}
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoKeys, buildInfoObject, buildInfoPackage}
import scoverage.ScoverageKeys.{coverageFailOnMinimum, coverageHighlighting, coverageMinimum}

object Settings {

  private object Options {
    lazy val scalaOptions: Seq[String] = Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused",
      "-Ywarn-value-discard",
      "-language:higherKinds"
    )

    lazy val styleOptions: Seq[Def.Setting[_]] = Seq(
      scalafmtOnCompile := true,
      scalastyleFailOnError := true,
      scalastyleFailOnWarning := true
    )
  }

  private lazy val buildInfoSettings: Seq[Def.Setting[_]] = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoObject := "AppInfo",
    buildInfoPackage := "com.mforest.example.application.info"
  )

  private lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    organization := "com.mforest.example",
    scalacOptions ++= Options.scalaOptions
  ) ++ Options.styleOptions

  private lazy val dockerSettings: Seq[Def.Setting[_]] = Seq(
    dockerUsername := Some("mforest"),
    dockerExposedPorts ++= Seq(
      sys.env.getOrElse("APP_PORT", "9000").toInt
    )
  )

  private lazy val noDockerSettings: Seq[Def.Setting[_]] = Seq(
    publish in Docker := {}
  )

  private lazy val coverageSettings: Seq[Def.Setting[_]] = Seq(
    coverageMinimum := 50,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )

  lazy val restrictions: Seq[Def.Setting[_]] = Seq(
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
  )

  lazy val root: Seq[Def.Setting[_]] = commonSettings ++ restrictions ++ noDockerSettings ++ coverageSettings

  lazy val application: Seq[Def.Setting[_]] = commonSettings ++ buildInfoSettings ++ dockerSettings ++
    Dependencies.application.asSettings ++ Testing.testSettings ++ Testing.e2eSettings

  private lazy val moduleSettings: Seq[Def.Setting[_]] = commonSettings ++ noDockerSettings ++ Testing.testSettings

  lazy val http: Seq[Def.Setting[_]]    = moduleSettings ++ Dependencies.http.asSettings ++ Testing.itSettings
  lazy val service: Seq[Def.Setting[_]] = moduleSettings ++ Dependencies.service.asSettings ++ Testing.itSettings
  lazy val db: Seq[Def.Setting[_]]      = moduleSettings ++ Dependencies.db.asSettings ++ Testing.itSettings
  lazy val core: Seq[Def.Setting[_]]    = moduleSettings ++ Dependencies.core.asSettings
}
