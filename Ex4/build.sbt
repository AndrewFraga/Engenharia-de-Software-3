name := "Ex4"
 
version := "1.0"
 
scalaVersion := "2.12.6"
 
cancelable in Global := true
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.16"