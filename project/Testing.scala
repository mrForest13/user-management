import Config.EndToEndTest
import sbt.Keys._
import sbt.{Def, _}

object Testing {

  lazy val testSettings: Seq[Def.Setting[_]] = Seq(
    fork in Test := false,
    parallelExecution in Test := true
  )

  lazy val itSettings: Seq[Def.Setting[_]] =
    inConfig(IntegrationTest)(Defaults.testSettings) ++ Seq(
      fork in IntegrationTest := false,
      parallelExecution in IntegrationTest := false,
      scalaSource in IntegrationTest := baseDirectory.value / "src" / "it" / "scala",
      resourceDirectory in IntegrationTest := baseDirectory.value / "src" / "it" / "resources"
    )

  lazy val e2eSettings: Seq[Def.Setting[_]] =
    inConfig(EndToEndTest)(Defaults.testSettings) ++ Seq(
      fork in EndToEndTest := false,
      parallelExecution in EndToEndTest := false,
      scalaSource in EndToEndTest := baseDirectory.value / "src" / "e2e" / "scala",
      resourceDirectory in EndToEndTest := baseDirectory.value / "src" / "e2e" / "resources"
    )
}
