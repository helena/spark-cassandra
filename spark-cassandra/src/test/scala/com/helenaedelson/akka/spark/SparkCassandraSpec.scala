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

import com.datastax.spark.connector.cql.CassandraConnector

import scala.math._
import scala.concurrent.duration._

class SparkCassandraSpec extends AbstractSpec {
  "SparkCassandra" must {

    val extension = SparkCassandra(system)
    import extension._

    "create a basic local context, run a function" in {
      extension.isTerminated should be(false)
      spark.isLocal should be(true)
      val slices = 2
      val n = 100000 * slices
      val count = spark.parallelize(1 to n, slices).map { i =>
        val x = random * 2 - 1
        val y = random * 2 - 1
        if (x * x + y * y < 1) 1 else 0
      }.reduce(_ + _)
      4.0 * count / n should be(3.13 +- .14778)
    }
    "work with basic RDD functions" in {
      val data = 1 to 10000
      val rdd = spark.parallelize(data)
      val result = rdd.filter(_ < 10).collect()
      result.last should be(9)
    }

    "read and write to cassandra" in {
      /* TODO work tests and then do automatic rollback
      CassandraConnector(spark.getConf).withSessionDo { session =>
        session.execute("CREATE KEYSPACE test2 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 }")
        session.execute("CREATE TABLE test2.words (word text PRIMARY KEY, count int)")
      }
      */
      import com.datastax.spark._

    }
    "cleanly shut down" in {
      extension.shutdown()
      awaitCond(extension.isTerminated, 1000.millis)
      extension.isTerminated should be(true)
    }
  }
}
