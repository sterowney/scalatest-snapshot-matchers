val ScalaVersion = "2.11.0"
val Organization = "commodityvectors"

lazy val core =
  Project("scalatest-snapshot-matcher-core", file("scalatest-snapshot-matcher-core"))
    .settings(
      organization := Organization,
      name := "scalatest-snapshot-matcher-core",
      scalaVersion := ScalaVersion
    )
    .settings(libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6",
      "com.googlecode.java-diff-utils" % "diffutils" % "1.2.1"
    ))

lazy val playJson =
  Project("scalatest-snapshot-matcher-play-json", file("scalatest-snapshot-matcher-play-json"))
    .settings(
      organization := Organization,
      name := "scalatest-snapshot-matcher-play-json",
      scalaVersion := ScalaVersion
    )
    .settings(libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.6.0-M6"
    )).dependsOn(core)