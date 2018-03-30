name := """star"""

version := "1.0"

scalaVersion := "2.11.11"

javacOptions ++= Seq("-encoding", "UTF-8")
javacOptions ++= Seq("-source", "1.8")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.google.protobuf" % "protobuf-java" % "3.5.1",
  "io.netty" % "netty-all" % "4.1.22.Final",
  "junit" % "junit" % "4.12" % "test",
 "org.slf4j" % "slf4j-api" % "1.7.25")
