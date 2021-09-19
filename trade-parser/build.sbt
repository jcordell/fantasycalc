val Http4sVersion = "0.23.1"
val CirceVersion = "0.14.1"
val MunitVersion = "0.7.27"
val LogbackVersion = "1.2.5"
val MunitCatsEffectVersion = "1.0.5"
val EnumeratumVersion = "1.7.0"
val DoobieVersion = "1.0.0-RC1"
val TestContainersScalaVersion = "0.39.7"

lazy val root = (project in file("."))
  .settings(
    organization := "fantasycalc",
    name := "trade-parser",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-specs2" % DoobieVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.1.1" % Test,
      "org.flywaydb" % "flyway-core" % "7.7.0",
      "com.dimafeng" %% "testcontainers-scala-scalatest" % TestContainersScalaVersion % "test",
      "com.dimafeng" %% "testcontainers-scala-postgresql" % TestContainersScalaVersion % "test",
      "io.getquill" %% "quill-sql" % "3.10.0",
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "mysql" % "mysql-connector-java" % "5.1.45",
      "com.beachape" %% "enumeratum" % EnumeratumVersion,
      "com.beachape" %% "enumeratum-doobie" % EnumeratumVersion,
      "org.scalameta" %% "svm-subs" % "20.2.0",
      "org.scalactic" %% "scalactic" % "3.2.9",
      "joda-time" % "joda-time" % "2.10.10",
      "org.joda" % "joda-convert" % "2.2.1",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test"
    ),
    addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.13.0" cross CrossVersion.full
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps",
      "-Ymacro-annotations"
    )
//    resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases"
  )

enablePlugins(DockerPlugin)

docker / dockerfile := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("openjdk:8-jre")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

/* FLYWAY CONFIG */
enablePlugins(FlywayPlugin)

val env = scala.util.Properties.envOrElse("SCALA_ENV", "")

val postgresDatabase = env match {
  case "test" =>
    scala.util.Properties
      .envOrElse("API_POSTGRES_TEST_DATABASE", "http4s_api_test")
  case _ =>
    scala.util.Properties.envOrElse("API_POSTGRES_DATABASE", "fantasycalc_db")
}

val postgresUser = env match {
  case "test" =>
    scala.util.Properties.envOrElse("API_POSTGRES_TEST_USER", "postgres")
  case _ => scala.util.Properties.envOrElse("API_POSTGRES_USER", "postgres")
}

val postgresPassword = env match {
  case "test" => scala.util.Properties.envOrElse("API_POSTGRES_TEST_PASS", "")
  case _      => scala.util.Properties.envOrElse("API_POSTGRES_PASS", "password")
}

flywayUrl := s"jdbc:postgresql:$postgresDatabase"
flywayUser := postgresUser
flywayPassword := postgresPassword
flywayLocations += "db/migration"
