enablePlugins(JavaAppPackaging)

name := "geotrellis_collections_api_ingest"
organization := ""
version := "1.0"
scalaVersion := "2.11.11"
javaOptions := Seq("-Xmx3072m", "-Xms2048m")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val scalaTestV  = "3.0.1"
  Seq(
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "org.locationtech.geotrellis" %% "geotrellis-spark" % "2.0.0-RC1",
    "org.locationtech.geotrellis" %% "geotrellis-s3" % "2.0.0-RC1",
    "org.apache.spark" %% "spark-core" % "2.3.0"
  )
}

assemblyMergeStrategy in assembly <<= (assemblyMergeStrategy in assembly) {
  (old) => {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case "reference.conf" | "application.conf"  => MergeStrategy.concat
    case x => MergeStrategy.first
  }
}

Revolver.settings
