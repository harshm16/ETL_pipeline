
name := "spark_app2"

version := "0.1"

scalaVersion := "2.11.12"

idePackagePrefix := Some("com.cc")
resolvers += "spark-core" at "https://mvnrepository.com/artifact/org.apache.spark/spark-core"

idePackagePrefix := Some("com.cc")
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
val logbackVersion = "1.3.0-alpha10"
val sfl4sVersion = "2.0.0-alpha5"
val typesafeConfigVersion = "1.4.1"
val scalacticVersion = "3.2.9"
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.slf4j" % "slf4j-api" % sfl4sVersion,
  "org.scalactic" %% "scalactic" % scalacticVersion,
  "org.scalatest" %% "scalatest" % scalacticVersion % Test,
  "org.scalatest" %% "scalatest-featurespec" % scalacticVersion % Test,
  "com.typesafe" % "config" % typesafeConfigVersion,
  "org.apache.spark" %% "spark-core" % "2.4.5" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.4.5" % "provided",
//  "org.apache.hbase.connectors.spark" % "hbase-spark" % "1.0.0",
  "com.amazonaws" % "aws-java-sdk-ses" % "1.12.111"
)
