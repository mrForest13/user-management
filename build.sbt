/*
  ROOT PROJECT
 */
lazy val root = (project in file("."))
  .configs(Config.all: _*)
  .settings(run := (run in application in Compile).evaluated)
  .settings(name := "root-user-management", Settings.root)
  .aggregate(
    application,
    core,
    db,
    http,
    service
  )

/*
  APPLICATION MODULE
 */
lazy val application = project
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .configs(Config.all: _*)
  .settings(name := "user-management", Settings.application)
  .dependsOn(core, db, service, http)

/*
  MODULES
 */
lazy val http = project
  .configs(Config.all: _*)
  .settings(name := "http", Settings.http)
  .dependsOn(core, service)

lazy val service = project
  .configs(Config.all: _*)
  .settings(name := "service", Settings.service)
  .dependsOn(core, db)

lazy val db = project
  .configs(Config.all: _*)
  .settings(name := "db", Settings.db)
  .dependsOn(core)

lazy val core = project
  .configs(Config.all: _*)
  .settings(name := "core", Settings.core)
