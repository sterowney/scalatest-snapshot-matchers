val ScalaVersion = "2.11.0"
val Organization = "commodityvectors"

lazy val core =
  Project("scalatest-snapshot-matcher-core", file("scalatest-snapshot-matcher-core"))
    .settings(
      organization := Organization,
      name := "scalatest-snapshot-matcher-core",
      scalaVersion := ScalaVersion,
      parallelExecution in Test := false
    )
    .settings(libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6",
      "com.googlecode.java-diff-utils" % "diffutils" % "1.2.1",
      "commons-io" % "commons-io" % "2.4" % "test"
    ))

lazy val playJson =
  Project("scalatest-snapshot-matcher-play-json", file("scalatest-snapshot-matcher-play-json"))
    .settings(
      organization := Organization,
      name := "scalatest-snapshot-matcher-play-json",
      scalaVersion := ScalaVersion,
      parallelExecution in Test := false
    )
    .settings(libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.6.0-M6",
      "commons-io" % "commons-io" % "2.4" % "test"
    )).dependsOn(core)