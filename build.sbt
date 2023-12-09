ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "my-notes",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.2"
    )
  )
