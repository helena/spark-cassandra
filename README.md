spark-cassandra
===============

An Akka Extension for easy integration of spark and cassandra in Akka micro services.

Documentation coming, this project is still being developed, but for now:

# Usage:
Where `system` is the the existing ActorSystem

val extension = SparkCassandra(system)
import extension._

val data = 1 to 10000
val result = spark.parallelize(data)
        .filter(_ < 10)
        .collect()

Spark Streaming is currently being added to the core SparkContext in place currently. I am adding it to the
driver project.

