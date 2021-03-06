import SonatypeKeys._
import sbt._

name := "httpmock-specs"

version := "0.6.1"

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4", "2.11.4")

resolvers ++= Seq(
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases", // to fix specs2 dependency
  "Sonatype" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.4.15" % "compile",
  "com.github.httpmock" % "mock-http-server-junit" % "1.1.8",
  "com.github.httpmock" % "mock-http-server-exec" % "1.1.8",
  "com.github.httpmock" % "mock-http-server-webapp" % "1.1.8" artifacts(Artifact("mock-http-server-webapp", "jar", "jar")) classifier ""
)

// Scoverage
scalacOptions in Test ++= Seq("-Yrangepos")

// publishing
pgpSecretRing := file("local.secring.gpg")

pgpPublicRing := file("local.pubring.gpg")

sonatypeSettings

organization := "com.github.httpmock"

pomExtra := {
  <url>https://github.com/renesca/renesca</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/httpmock/mock-http-server-specs</url>
      <connection>scm:git:git@github.com:httpmock/mock-http-server-specs.git</connection>
    </scm>

    <developers>
      <developer>
        <id>snordquist</id>
        <name>Sascha Nordquist</name>
        <url>https://github.com/snordquist</url>
      </developer>
    </developers>
}

pomPostProcess := { (pomXML: scala.xml.Node) =>
  PomPostProcessor(pomXML)
}

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Yinline", "-Yinline-warnings",
  "-language:_"
)

// support source folders for version specific code
unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion) {
  (s, v) => s / ("scala_" + v)
}

net.virtualvoid.sbt.graph.Plugin.graphSettings
