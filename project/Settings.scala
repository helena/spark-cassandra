/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cassandra

import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._
import com.typesafe.tools.mima.plugin.MimaKeys._
import com.typesafe.tools.mima.plugin.MimaPlugin._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform._

object Settings extends Build {

  lazy val buildSettings = Seq(
    name := "spark-cassandra",
    organization := "com.helenaedelson",
    scalaVersion := Versions.Scala,
    homepage := Some(url("https://github.com/helena/spark-cassandra")),
    licenses in ThisBuild := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    resolvers ++= Seq(
      "Mvn Releases" at "http://repo1.maven.org/maven2/"
    )
  )

  override lazy val settings = super.settings ++ buildSettings ++ Seq(shellPrompt := ShellPrompt.prompt)

  lazy val baseSettings = Defaults.defaultSettings // ToDo ++ Publish.settings

  lazy val parentSettings = baseSettings ++ Seq(
    publishArtifact := false,
    reportBinaryIssues := () // disable bin comp check
  )

  lazy val defaultSettings = baseSettings ++ testSettings ++ mimaSettings ++ releaseSettings ++ formatSettings ++ Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", s"-target:jvm-${Versions.JDK}", "-deprecation", "-feature", "-language:_", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions ++= Seq("-encoding", "UTF-8", "-source", Versions.JDK, "-target", Versions.JDK, "-Xlint:unchecked", "-Xlint:deprecation"),
    ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet
  )

  lazy val mimaSettings = mimaDefaultSettings ++ Seq(
    previousArtifact := None
  )

  val tests = inConfig(Test)(Defaults.testTasks)

  val testOptionSettings = Seq(
    Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
    Tests.Argument(TestFrameworks.JUnit, "-oDF")
  )

  lazy val testSettings = tests ++ Seq(
    parallelExecution in Test := false,
    parallelExecution in IntegrationTest := false,
    testOptions in Test ++= testOptionSettings,
    testOptions in IntegrationTest ++= testOptionSettings,
    fork in Test := true
  )

  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile  := formattingPreferences,
    ScalariformKeys.preferences in Test     := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }
}

/**
 * TODO make plugin
 * Shell prompt which shows the current project, git branch
 */
object ShellPrompt {

  def gitBranches = "git branch" lines_! devnull mkString

  def current: String = """\*\s+([\.\w-]+)""".r findFirstMatchIn gitBranches map (_ group 1) getOrElse "-"

  def currBranch: String = ("git status -sb" lines_! devnull headOption) getOrElse "-" stripPrefix "## "

  lazy val prompt = (state: State) =>
    "%s:%s:%s> ".format("strikeserver", Project.extract (state).currentProject.id, currBranch)

  object devnull extends ProcessLogger {
    def info(s: => String) {}
    def error(s: => String) {}
    def buffer[T](f: => T): T = f
  }
}