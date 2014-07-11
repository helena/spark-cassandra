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

import com.datastax.driver.spark.connector.CassandraConnector

import scala.math._

class SparkCassandraSpec extends AbstractSpec {
  "SparkCassandra" must {
    "create a basic local context" in {
      val extension = SparkCassandra(system)
      extension.isTerminated should be(false)
      import extension._

      val slices = 2
      val n = 100000 * slices
      val count = spark.parallelize(1 to n, slices).map { i =>
        val x = random * 2 - 1
        val y = random * 2 - 1
        if (x*x + y*y < 1) 1 else 0
      }.reduce(_ + _)
      4.0 * count / n should be (3.13 +- .14778)
    }

    "read and write to cassandra" in {
      val extension = SparkCassandra(system)

      CassandraConnector(extension.spark.getConf).withSessionDo { session =>
        session.execute("CREATE KEYSPACE test2 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 }")
        session.execute("CREATE TABLE test2.words (word text PRIMARY KEY, count int)")
      }

      /* TODO work tests and then delete test table */
      import extension._
      import com.datastax.driver.spark._
      
    }
  }
}
