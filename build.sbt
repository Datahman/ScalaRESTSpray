name := "FirstRESTSPRAY"

version := "0.1"

scalaVersion := "2.11.2"



scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.17"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % "1.3.1",
    "io.spray"            %%  "spray-routing" % "1.3.3",
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "io.spray"            %%  "spray-http"    % "1.3.3",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-http"     % "10.0.10",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.json4s"          %% "json4s-native"  % "3.3.0",
    "org.json4s"          %% "json4s-ext"     % "3.2.11",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scalaz"          %%  "scalaz-core"   % "7.1.0",
    "org.scalactic"       %% "scalactic"      % "3.0.4",
    "org.scalatest"       %% "scalatest"      % "3.0.4" % "test"

  )
}

resolvers ++= Seq(
  "Spray repository" at "http://repo.spray.io",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)


lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

