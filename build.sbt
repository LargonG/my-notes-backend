ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.kote"
ThisBuild / scalaVersion := "2.13.12"

val catsVersion = "2.9.0"
val catsEffect3 = "3.4.8"
val scalatestVersion = "3.2.15"
val scalamockVersion = "5.2.0"
val kindProjectorVersion = "0.13.2"

lazy val root = (project in file("."))
  .settings(
    name := "my-notes",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffect3,
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.scalamock" %% "scalamock" % scalamockVersion % Test,
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  )

