name := "demo"

version := "1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.7"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-Xcheckinit",
                      "-Xlint", "-Xfatal-warnings", "-g:line",
                      "-Ywarn-dead-code",  "-Ywarn-numeric-widen")

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor"       % akkaVersion     % Compile,
    "com.typesafe.akka" %% "akka-testkit"     % akkaVersion     % Test,
    "org.scalatest"     %% "scalatest"        % "2.2.6"         % Test,
    "com.typesafe.akka" %% "akka-slf4j"       % akkaVersion     % Runtime,
    "ch.qos.logback"    %  "logback-classic"  % "1.1.7"         % Runtime
  )
}

mainClass in (Compile, run) := Some("org.derekmorr.Server")
