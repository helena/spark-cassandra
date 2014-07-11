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

/**
 * TODO add sbt release
 */
object Publish extends Build {

  lazy val publishSettings = Seq(
    publishTo <<= isSnapshot { isSnapshot =>
      val id = if (isSnapshot) "content/repositories/snapshots" else "service/local/staging/deploy/maven2"
      val uri = s"https://oss.sonatype.org/$id"
      Some(Resolver.url(uri, url(uri))(Resolver.ivyStylePatterns))
    },
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in Test := false,
    publishMavenStyle := true,
    pomIncludeRepository := { x => false },
    pomExtra := (
      <url>http://github.com/helena/spark-cassandra</url>
        <scm>
          <url>git@github.com:helena/spark-cassandra.git</url>
          <connection>scm:git:git@github.com:helena/spark-cassandra.git</connection>
        </scm>
        <developers>
          <developer>
            <id>helena</id>
            <name>Helena Edelson</name>
            <url>http://github.com/helena</url>
          </developer>
        </developers>
      )
  )
}