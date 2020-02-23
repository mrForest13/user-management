import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import org.scalastyle.sbt.ScalastylePlugin.autoImport.{scalastyleFailOnError, scalastyleFailOnWarning}
import sbt.Def
import sbt.Keys.{organization, scalaVersion, version, _}
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoKeys, buildInfoPackage}

object Settings {

  import Dependencies._

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
    buildInfoPackage := "com.mforest.example.application.info"
  )

  private lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    organization := "com.mforest.example",
    scalacOptions ++= Options.scalaOptions
  ) ++ Options.styleOptions

  lazy val root: Seq[Def.Setting[_]] = commonSettings

  lazy val application: Seq[Def.Setting[_]] = commonSettings ++ buildInfoSettings ++ Dependencies.application.asSettings

  private lazy val moduleSettings: Seq[Def.Setting[_]] = commonSettings

  lazy val http: Seq[Def.Setting[_]]    = moduleSettings ++ Dependencies.http.asSettings
  lazy val service: Seq[Def.Setting[_]] = moduleSettings ++ Dependencies.service.asSettings
  lazy val db: Seq[Def.Setting[_]]      = moduleSettings ++ Dependencies.db.asSettings
  lazy val core: Seq[Def.Setting[_]]    = moduleSettings ++ Dependencies.core.asSettings
}
