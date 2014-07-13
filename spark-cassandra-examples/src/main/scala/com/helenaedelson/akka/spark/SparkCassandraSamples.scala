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

import com.typesafe.config.{ConfigFactory, Config}

import org.apache.spark._

/**
 * Stubbed out for future samples.
 *
 * @author Helena Edelson
 */

object SparkCassandraSamples {
  val base: Config = ConfigFactory.parseString(
    """
      |spark-cassandra {
      |  spark {
      |    app-name  = "test app"
      |    load-defaults = false
      |  }
      |
      |  cassandra {
      |    name = "MyDemoCluster"
      |    keyspace = "test_keyspace"
      |    connection.seed-nodes = ["127.0.0.1"]
      |  }
      |}
      |
      |akka {
      |  loglevel = "DEBUG"
      |
      |  actor {
      |    debug {
      |      receive = off
      |      event-stream = off
      |    }
      |  }
      |
      |  remote {
      |    netty.tcp.port = 0
      |    log-remote-lifecycle-events = on
      |    log-received-messages = on
      |    log-sent-messages = on
      |  }
      |}
    """.stripMargin)
}

abstract class SparkCassandraSamples(config: Config) extends Logging {
  def this() = this(SparkCassandraSamples.base)

}