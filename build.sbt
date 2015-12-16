releaseCrossBuild := true

name := "play-prerender"

scalaVersion := "2.11.4"

organization := "net.kaliber"

crossScalaVersions := Seq("2.10.4", scalaVersion.value)

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.4.0" % "provided",
  "com.typesafe.play" %% "play-ws" % "2.4.0" % "provided"
)

publishTo := kaliberRepo(version.value)

def kaliberRepo(version: String) = {
  val repo = if (version endsWith "SNAPSHOT") "snapshot" else "release"
  Some("Kaliber " + repo.capitalize + " Repository" at "https://jars.kaliber.io/artifactory/libs-" + repo + "-local")
}
