ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.kote"
ThisBuild / scalaVersion := "2.13.12"

val catsVersion = "2.9.0"
val catsEffect3 = "3.4.8"
val catsBackendVersion = "3.8.13"

val sttpClientVersion = "3.9.0"
val tapirVersion = "1.7.6"
val http4sVersion = "0.23.23"

val pureConfigVersion = "0.17.4"

val flywayVersion = "9.16.0"

val quillVersion = "4.6.0"
val doobieVersion = "1.0.0-RC2"

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

      // client
      "com.softwaremill.sttp.client3" %% "core" % sttpClientVersion,
      "com.softwaremill.sttp.client3" %% "circe" % sttpClientVersion,
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % catsBackendVersion,

      // http4s
      "org.http4s" %% "http4s-ember-server" % http4sVersion,

      // logback
      "ch.qos.logback" % "logback-core" % logbackVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,

      // tethys (json)
      "com.tethys-json" %% "tethys-core" % tethysVersion,
      "com.tethys-json" %% "tethys-jackson" % tethysVersion,
      "com.tethys-json" %% "tethys-derivation" % tethysVersion,
      "com.tethys-json" %% "tethys-enumeratum" % tethysVersion,
      "com.tethys-json" %% "tethys-circe" % tethysVersion,

      // enumeratum
      "com.beachape" %% "enumeratum" % enumeratumVersion,

      // doobie + quill
      "io.getquill" %% "quill-doobie" % quillVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,

      // circe
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // pureconfig
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,

      // flyway
      "org.flywaydb" % "flyway-core" % flywayVersion,

      // tests
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.scalamock" %% "scalamock" % scalamockVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % testVersion % Test,
      "org.scalatestplus" %% "mockito-4-11" % mockitoVersion % Test,

      // Ungrouped
      "com.github.cb372" %% "cats-retry" % catsRetryVersion,
      "org.typelevel" %% "log4cats-core" % catsLoggingVersion,
      "org.typelevel" %% "log4cats-slf4j" % catsLoggingVersion,
      "com.dimafeng" %% "testcontainers-scala-scalatest" % testContainersVersion,
      "com.typesafe" % "config" % configVersion,
      "com.iheart" %% "ficus" % ficusVersion,
      "com.softwaremill.macwire" %% "macros" % wireVersion % Provided,
      "com.softwaremill.macwire" %% "util" % wireVersion,
      "com.softwaremill.macwire" %% "proxy" % wireVersion,
    ),
    addCompilerPlugin(
      ("org.typelevel" % "kind-projector" % kindProjectorVersion).cross(CrossVersion.full),
    ),
  )
  .enablePlugins(JavaAppPackaging)

val tethysVersion = "0.26.0"
val circeVersion = "0.14.1"

val enumeratumVersion = "1.7.2"

val scalatestVersion = "3.2.15"
val scalamockVersion = "5.2.0"
val kindProjectorVersion = "0.13.2"
val logbackVersion = "1.4.7"
val testVersion = "1.4.0"
val mockitoVersion = "3.2.16.0"
val configVersion = "1.4.2"
val ficusVersion = "1.5.2"
val wireVersion = "2.5.8"
val wireMockVersion = "3.0.0"
val testContainersVersion = "0.40.15"
val catsRetryVersion = "3.1.0"
val catsLoggingVersion = "2.6.0"
