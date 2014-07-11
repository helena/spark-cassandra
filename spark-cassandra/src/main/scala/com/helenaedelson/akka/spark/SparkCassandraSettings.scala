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

package com.helenaedelson.akka.spark

import scala.collection.immutable
import scala.concurrent.duration.{ Duration, FiniteDuration }
import akka.japi.Util.immutableSeq
import com.typesafe.config.Config

import java.util.concurrent.TimeUnit.{ MILLISECONDS ⇒ MILLIS }
import java.net.URI

/**
 * @author Helena Edelson
 */
final class SparkCassandraSettings(val config: Config) {

  private val sc = config.getConfig("spark-cassandra")
  private val spark = sc.getConfig("spark")
  private val cassandra = sc.getConfig("cassandra")

  val SparkMasterURI: String = spark.getString("master") match {
    case host if host.nonEmpty ⇒ new URI(s"spark://$host:${spark.getInt("port")}").toString
    case _                     ⇒ "local"
  }

  val SparkAppName: String = Option(spark.getString("app-name")) getOrElse "Test"
  val SparkLoadJars: immutable.IndexedSeq[String] = immutableSeq(spark.getStringList("load-jars")).toVector

  val CassandraClusterName: String = cassandra.getString("name")
  val CassandraKeyspace: String = cassandra.getString("keyspace")
  val CassandraSeedNodes: immutable.IndexedSeq[String] = immutableSeq(cassandra.getStringList("connection.seed-nodes")).toVector

  /* Auth */
  val CassandraAuthUsername: String = cassandra.getString("connection.auth.username")
  val CassandraAuthPassword: String = cassandra.getString("connection.auth.password")
  val CassandraAuthFactoryClass: String = cassandra.getString("connection.auth.auth-impl")

  /* Optional environment configurations and tuning, with fallback to defaults. */
  val CassandraRpcPort: Int = cassandra.getInt("connection.rpc-port")
  val CassandraCqlPort: Int = cassandra.getInt("connection.native-port")
  val CassandraKeepAlive: Duration = Duration(cassandra.getMilliseconds("connection.keep-alive"), MILLIS)
  val CassandraRetryCount: Int = cassandra.getInt("connection.retry-count")
  val CassandraReconnectUnreachableAfterMinDelay: Duration =
    Duration(cassandra.getMilliseconds("connection.reconnect-delay.min"), MILLIS)
  val CassandraReconnectUnreachableAfterMaxDelay: Duration =
    Duration(cassandra.getMilliseconds("connection.reconnect-delay.max"), MILLIS)

  /* Output */
  val CassandraConcurrentWrites: Int = cassandra.getInt("output.concurrent-writes")
  val CassandraBatchMaxBytes: String = cassandra.getString("output.max-bytes")
  val CassandraBatchRowSize: Option[Int] = {
    val NumberPattern = "([0-9]+)".r
    cassandra.getAnyRef("output.row-size") match {
      case NumberPattern(x) ⇒ Some(x.toInt)
      case _                ⇒ None // auto or unsupported
    }
  }

  /* Input */
  val CassandraPagedRows: Int = cassandra.getInt("input.paged-row-count")
  val CassandraSplitSize: Int = cassandra.getInt("input.task-row-count")

  val CassandraOptions: immutable.Set[(String, String)] = Set(
    ("cassandra.connection.host", CassandraSeedNodes.head), //TODO
    ("cassandra.connection.rpc.port", CassandraRpcPort.toString),
    ("cassandra.connection.native.port", CassandraRpcPort.toString),
    ("cassandra.connection.keep_alive_ms", CassandraKeepAlive.toMillis.toString),
    ("cassandra.connection.reconnection_delay_ms.min", CassandraReconnectUnreachableAfterMinDelay.toString),
    ("cassandra.connection.reconnection_delay_ms.max", CassandraReconnectUnreachableAfterMaxDelay.toString),
    ("cassandra.input.split.size", CassandraSplitSize.toString),
    ("cassandra.input.page.row.size", CassandraPagedRows.toString),
    // TODO add CassandraBatchMaxBytes, CassandraConcurrentWrites, CassandraBatchRowSize
    ("cassandra.query.retry.count", CassandraRetryCount.toString),
    ("cassandra.username", CassandraAuthUsername),
    ("cassandra.password", CassandraAuthPassword)/*,
    ("cassandra.auth.conf.factory.class", CassandraAuthFactoryClass)*/)
}
