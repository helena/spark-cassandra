/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
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

object SparkCassandraBuild extends Build {

  import Settings._

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = parentSettings,
    aggregate = Seq(extension, examples)
  )

  lazy val extension = LibraryProject("spark-cassandra", Dependencies.extension)

  lazy val examples = LibraryProject("spark-cassandra-examples", Dependencies.examples)

  def LibraryProject(name: String, deps: Seq[ModuleID], cpd: Seq[ClasspathDep[ProjectReference]] = Seq.empty): Project =
    Project(name, file(name), settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.testkit ++ deps), dependencies = cpd)

}

object Dependencies {

  object Compile {
    import Versions._

    val akkaCluster       = "com.typesafe.akka"           %% "akka-cluster"            % Akka             % "provided"  // ApacheV2
    val cassSparkDriver   = "com.datastax.cassandra"      %% "cassandra-driver-spark"  % "1.0.0-SNAPSHOT" // TODO remove snapshot when they publish :(
    val lzf               = "com.ning"                    % "compress-lzf"          % Lzf            % "provided"
    val sparkStreaming    = "org.apache.spark"            %% "spark-streaming"         % Spark            % "provided"  // ApacheV2

    object Metrics {
      val hdrHistogram = "org.hdrhistogram"              % "HdrHistogram"              % HdrHistogram     % "test"      // CC0
      val latencyUtils = "org.latencyutils"              % "LatencyUtils"              % LatencyUtils     % "test"      // Free BSD
      val metrics      = "com.codahale.metrics"          % "metrics-core"              % CodahaleMetrics  % "test"      // ApacheV2
      val metricsJvm   = "com.codahale.metrics"          % "metrics-jvm"               % CodahaleMetrics  % "test"      // ApacheV2
    }

    object Test {
      val akkaTestKit   = "com.typesafe.akka"           %% "akka-testkit"              % Akka             % "test"      // ApacheV2
      val scalatest     = "org.scalatest"               %% "scalatest"                 % ScalaTest        % "test"      // ApacheV2
    }
  }

  import Compile._

  val metrics = Seq(Metrics.metrics, Metrics.metricsJvm, Metrics.latencyUtils, Metrics.hdrHistogram)

  val testkit = Seq(Test.akkaTestKit, Test.scalatest)

  val extension = metrics ++ Seq(akkaCluster, cassSparkDriver, sparkStreaming)

  val examples = metrics ++ Seq(akkaCluster, cassSparkDriver, sparkStreaming)

}
