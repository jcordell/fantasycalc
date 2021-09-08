val Http4sVersion = "0.23.1"
val CirceVersion = "0.14.1"
val MunitVersion = "0.7.27"
val LogbackVersion = "1.2.5"
val MunitCatsEffectVersion = "1.0.5"
val EnumeratumVersion = "1.7.0"

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
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.beachape" %% "enumeratum" % EnumeratumVersion,
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
    testFrameworks += new TestFramework("munit.Framework")
//    resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases"
  )
