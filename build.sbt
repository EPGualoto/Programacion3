ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "grupal"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.1",
  "org.apache.spark" %% "spark-sql" % "3.5.1" % "provided",
  "org.apache.spark" %% "spark-mllib" % "3.5.1",
  "org.scalanlp" %% "breeze-viz" % "2.1.0",

    "org.scalanlp" %% "breeze" % "2.1.0",



)

