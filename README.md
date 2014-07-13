spark-cassandra
===============

An [Akka Extension](http://doc.akka.io/docs/akka/snapshot/intro/what-is-akka.html) for easy integration of [Spark](http://spark.apache.org) and [Cassandra](http://cassandra.apache.org) in Akka micro services, etc.

[Spark Streaming](http://spark.apache.org/streaming/) is currently being added to the core SparkContext in place currently. I am adding it to the [connector project]: (https://github.com/datastax/spark-cassandra-connector/issues/55)

Full documentation coming, this project is still being developed, but for now:

# Usage:

## Configuration
Coming soon.

## Basic Usage for Spark
Where `system` is the the existing ActorSystem

```scala
val extension = SparkCassandra(system)
import extension._

val data = 1 to 10000
val results = spark.parallelize(data)
  .filter(_ < 10)
  .collect()
```

## Basic Usage for Cassandra
Coming Soon.

## Easy Integration of Spark and Cassandra in Akka
Coming Soon.