Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / baseVersion := "0.1.0"
ThisBuild / organization := "uk.co.odinconsultants"
ThisBuild / publishGithubUser := "PhillHenry"
ThisBuild / publishFullName := "Phillip Henry"

replaceCommandAlias("ci","; project /; headerCheckAll; clean; testIfRelevant; docs/mdoc; mimaReportBinaryIssuesIfRelevant")

// sbt-sonatype wants these in Global
Global / homepage := Some(url("https://github.com/PhillHenry/FS2Playground"))
Global / scmInfo := Some(ScmInfo(url("https://github.com/PhillHenry/FS2Playground"), "git@github.com:PhillHenry/FS2Playground.git"))
Global / excludeLintKeys += scmInfo
ThisBuild / spiewakMainBranches := Seq("main")

val scala3 = "3.3.0"
ThisBuild / crossScalaVersions := Seq(scala3, "2.12.10", "2.13.4")
ThisBuild / versionIntroduced := Map(scala3 -> scala3)

ThisBuild / initialCommands := """
  |import cats._, data._, syntax.all._
  |import cats.effect._, concurrent._, implicits._
  |import fs2._
  |import fs2.concurrent._
  |import scala.concurrent.duration._
  |import uk.co.odinconsultants._
""".stripMargin

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

lazy val root = project
  .in(file("."))
  .settings(
    name := "F22Playground"
  )
  .enablePlugins(NoPublishPlugin, SonatypeCiReleasePlugin)
  .aggregate(core)


lazy val core = project
  .in(file("modules/core"))
  .settings(
    name := "FS2Playground-core",
    scalafmtOnCompile := true,
    libraryDependencies ++=
      dep("org.typelevel", "cats-effect", "3.1.1")("")("-laws") ++
      dep("co.fs2", "fs2-", "3.2.7")("core", "io")() ++
      dep("org.scalameta", "munit", "0.7.26")()("", "-scalacheck") ++
      dep("org.typelevel", "", "0.13.0")()("munit-cats-effect-3") ++ dep("org.typelevel",  "scalacheck-effect", "0.6.0")()("", "-munit")
  )

lazy val docs = project
  .in(file("docs"))
  .settings(
    mdocIn := file("modules/docs"),
    mdocOut := file("docs"),
    mdocVariables := Map("VERSION" -> version.value),
    githubWorkflowArtifactUpload := false
  ).dependsOn(core)
   .enablePlugins(MdocPlugin, NoPublishPlugin)

def dep(org: String, prefix: String, version: String)(modules: String*)(testModules: String*) =
  modules.map(m => org %% (prefix ++ m) % version) ++
   testModules.map(m => org %% (prefix ++ m) % version % Test)

ThisBuild / scalacOptions ++= Seq(
  "-encoding", "utf8", // Option and arguments on same line
  "-Xfatal-warnings",  // New lines for each options
  "-Ywarn-value-discard",
  "-Wunused",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)