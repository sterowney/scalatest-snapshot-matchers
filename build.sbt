val ScalaVersion = "2.11.10"
val Organization = "com.commodityvectors"

val sharedSettings = Seq(
  organization := Organization,
  scalaVersion := ScalaVersion,
  parallelExecution in Test := false,
  bintrayOrganization := Some("commodityvectors"),
  bintrayRepository := "commodityvectors-releases",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  pomExtra := (
    <scm>
      <url>git@github.com:commodityvectors/scalatest-snapshot-matcher.git</url>
      <connection>scm:git:git@github.com:commodityvectors/scalatest-snapshot-matcher.git</connection>
    </scm>
  ),
  releaseTagComment := s"[${name.value}] Releasing ${version.value}",
  releaseCommitMessage := s"[${name.value}] Setting version to ${version.value}",
  releaseTagName := s"${name.value}v${version.value}"
)

lazy val root = Project("scalatest-snapshot-matcher", file("."))
  .settings(publish := {})
  .settings(publishArtifact := false)
  .settings(name := "scalatest-snapshot-matcher")
  .settings(sharedSettings: _*)
  .aggregate(core, playJson)

lazy val core =
  Project("scalatest-snapshot-matcher-core", file("scalatest-snapshot-matcher-core"))
    .settings(name := "scalatest-snapshot-matcher-core")
    .settings(sharedSettings: _*)
    .settings(libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6",
      "com.googlecode.java-diff-utils" % "diffutils" % "1.2.1",
      "com.typesafe" % "config" % "1.3.1",
      "commons-io" % "commons-io" % "2.4" % "test"
    ))

lazy val playJson =
  Project("scalatest-snapshot-matcher-play-json", file("scalatest-snapshot-matcher-play-json"))
    .settings(name := "scalatest-snapshot-matcher-play-json")
    .settings(sharedSettings: _*)
    .settings(libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.6.0-M6",
      "commons-io" % "commons-io" % "2.4" % "test"
    )).dependsOn(core)
