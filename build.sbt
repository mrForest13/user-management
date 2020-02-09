/*
  ROOT PROJECT
 */
lazy val root = (project in file("."))
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
  .settings(name := "user-management", Settings.application)
  .dependsOn(core, db, service, http)

/*
  MODULES
 */
lazy val http = project
  .settings(name := "http", Settings.http)
  .dependsOn(core, service)

lazy val service = project
  .settings(name := "service", Settings.service)
  .dependsOn(core, db)

lazy val db = project
  .settings(name := "db", Settings.db)
  .dependsOn(core)

lazy val core = project
  .settings(name := "core", Settings.core)
