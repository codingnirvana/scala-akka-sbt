import sbt._
import sbt.Keys._

object AkkascalasbtBuild extends Build {

  import Resolvers._
  import Dependencies._

  lazy val akkascalasbt = Project(
    id = "akka-scala-sbt",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "akka-scala-sbt",
      organization := "indix",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers := Seq(sonatypeRepo, typesafeRepo),
      libraryDependencies ++= Seq(
        akkaActor, akkaRemote, asyncHttp,
        scalatest, jettyServerTest, akkaTestKit
      )
    )
  )

  object Resolvers {
    val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
    val typesafeRepo = "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
  }

  object Dependencies {
    val akkaVersion: String = "2.0.1"
    val akkaActor = "com.typesafe.akka" % "akka-actor" % akkaVersion
    val akkaRemote = "com.typesafe.akka" % "akka-remote" % akkaVersion
    val asyncHttp = "com.ning" % "async-http-client" % "1.7.5"

    val scalatest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
    val akkaTestKit = "com.typesafe.akka" % "akka-testkit" % akkaVersion % "test"
    val jettyVersion = "7.4.0.v20110414"
    val jettyServerTest = "org.eclipse.jetty" % "jetty-server" % jettyVersion % "test"
  }

}
