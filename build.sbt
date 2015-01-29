name := "play-prerender"

version := "0.2"

scalaVersion := "2.11.2"

organization := "nl.rhinofly"

crossScalaVersions := Seq("2.10.4", "2.11.2")

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  rhinoflyRepo("RELEASE").get
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.0" % "provided",
  "com.typesafe.play" %% "play-ws" % "2.3.0" % "provided")

publishTo := rhinoflyRepo(version.value)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")  

def rhinoflyRepo(version: String) = {
    val repo = if (version endsWith "SNAPSHOT") "snapshot" else "release"
    Some("Rhinofly Internal " + repo.capitalize + " Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-" + repo + "-local")
}
