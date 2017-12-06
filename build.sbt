val globalSettings = Seq[SettingsDefinition](
  version := "0.1",
  scalaVersion := "2.12.4",
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-feature", "-deprecation"),
    libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test"
  )
)

lazy val core = Project("core", file("core"))
  .settings(globalSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser",
      "io.circe" %% "circe-generic-extras"
    ).map(_ % "0.8.0"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )

lazy val root = project.in(file("."))
  .aggregate(core)
