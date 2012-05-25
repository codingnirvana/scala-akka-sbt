import sbt._
import sbt.Keys._

object AkkascalasbtBuild extends Build {

  lazy val akkascalasbt = Project(
    id = "akka-scala-sbt",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "akka-scala-sbt",
      organization := "indix",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1"
    )
  )
}
