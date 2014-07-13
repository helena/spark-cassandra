spark-cassandra
===============

An Akka Extension for easy integration of spark and cassandra in Akka micro services.

Spark Streaming is currently being added to the core SparkContext in place currently. I am adding it to the
driver project: (https://github.com/datastax/spark-cassandra-connector/issues/55)

Documentation coming, this project is still being developed, but for now:

# Usage:

## Configuration
Coming soon.

## Basic Usage for Spark
Where `system` is the the existing ActorSystem

```scala
val extension = SparkCassandra(system)
import extension._

val data = 1 to 10000
val result = spark.parallelize(data).filter(_ < 10).collect()
```

## Basic Usage for Cassandra
Coming Soon.

## Easy Integration of Spark and Cassandra in Akka
Coming Soon.