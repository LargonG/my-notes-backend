ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.kote"
ThisBuild / scalaVersion := "2.13.12"

val catsVersion = "2.9.0"
val catsEffect3 = "3.4.8"
val scalatestVersion = "3.2.15"
val scalamockVersion = "5.2.0"
val kindProjectorVersion = "0.13.2"

val tapirVersion = "1.7.6"
val http4sVersion = "0.23.23"
val logbackVersion = "1.4.11"
val tethysVersion = "0.26.0"
val enumeratumVersion = "1.7.2"

val circeVersion = "0.14.1"

lazy val root = (project in file("."))
  .settings(
    name := "my-notes",
    libraryDependencies ++= Seq(
      // cats
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffect3,

      // tapir
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-tethys" % tapirVersion,

      // http4s
      "org.http4s" %% "http4s-ember-server" % http4sVersion,

      // logback
      "ch.qos.logback" % "logback-classic" % logbackVersion,

      // tethys (json)
      "com.tethys-json" %% "tethys-core" % tethysVersion,
      "com.tethys-json" %% "tethys-jackson" % tethysVersion,
      "com.tethys-json" %% "tethys-derivation" % tethysVersion,
      "com.tethys-json" %% "tethys-enumeratum" % tethysVersion,
      "com.tethys-json" %% "tethys-circe" % tethysVersion,

      // enumeratum
      "com.beachape" %% "enumeratum" % enumeratumVersion,

      // circe
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // tests
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.scalamock" %% "scalamock" % scalamockVersion % Test,
    ),
    addCompilerPlugin(
      ("org.typelevel" % "kind-projector" % kindProjectorVersion).cross(CrossVersion.full),
    ),
  )
  .enablePlugins(JavaAppPackaging)