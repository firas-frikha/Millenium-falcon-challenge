ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "MillenniumFalconBackend"
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

dependencyOverrides += "org.slf4j" % "slf4j-api" % "1.7.36"

val AkkaVersion = "2.10.0"
val AkkaHttpVersion = "10.7.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,

  "ch.qos.logback" % "logback-classic" % "1.2.13",

  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "9.0.1",
)