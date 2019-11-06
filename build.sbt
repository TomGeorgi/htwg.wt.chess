name := """Chess in Scala for WebTech"""

version := "1.6"

lazy val root = (project in file (".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

libraryDependencies += guice

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += "com.h2database" % "h2" % "1.4.196"