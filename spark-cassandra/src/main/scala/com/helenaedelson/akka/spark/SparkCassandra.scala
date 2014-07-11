package com.helenaedelson.akka.spark

import java.util.concurrent.atomic.AtomicBoolean
import scala.reflect.ClassTag

import akka.actor._
import com.datastax.driver.spark.rdd.CassandraRDD
import com.datastax.driver.spark.rdd.reader.RowReaderFactory
import org.apache.spark.rdd.RDD
import org.apache.spark.{ Logging, SparkContext, SparkConf }

/**
 * SparkCassandra Extension is and factory for creating SparkCassandra extension.
 *
 * @author Helena Edelson
 */
object SparkCassandra extends ExtensionId[SparkCassandra] with ExtensionIdProvider {
  override def get(system: ActorSystem): SparkCassandra = super.get(system)

  override def lookup = SparkCassandra

  override def createExtension(system: ExtendedActorSystem): SparkCassandra = new SparkCassandra(system)
}

class SparkCassandra(val system: ExtendedActorSystem) extends Extension with Logging {

  system.registerOnTermination(shutdown())

  val settings = new SparkCassandraSettings(system.settings.config)
  import settings._

  // TBD val cluster = Cluster(system)

  private val _isTerminated = new AtomicBoolean(false)

  /**
   * Configures the [[SparkConf]] and informs Spark of the address of one Cassandra node.
   * Adds a JAR dependency for all tasks to be executed on this SparkContext in the future.
   * The `path` passed can be either a local file, a file in HDFS (or other Hadoop-supported
   * filesystems), an HTTP, HTTPS or FTP URI, or local:/path for a file on every worker node.
   *
   * The master URL to connect to, such as "local" to run locally with one thread, "local[4]" to
   * run locally with 4 cores, or "spark://master:7077" to run on a Spark standalone cluster.
   */
  private val config = new SparkConf(true)
    .setAppName(SparkAppName)
    .setJars(SparkLoadJars)
    .setMaster(SparkMasterURI)
    .setAll(CassandraOptions)

  val spark: SparkContext = new SparkContext(config)

  log.info(s"$spark created.")

  import com.datastax.driver.spark._

  /**
   * Returns a view of a Cassandra table as `CassandraRDD` of type T for CassandraRow tuples.
   * These are serialized and sent to every Spark executor.
   *
   * {{{
   *   // returns CassandraRDD[String]
   *   val rdd1 = spark.table("ks", "my_table").select("column1").as((s: String) => s)
   *
   *   // returns tuple fields CassandraRDD[(String, Long)]
   *   val rdd2 = spark.table("ks", "my_table").select("column1", "column2").as((_: String, _: Long))
   *
   *   // If T is: case class MyRow(key: String, value: Long), returns CassandraRDD[MyRow]
   *   val rdd3 = spark.table("ks", "my_table").select("column1", "column2").as(MyRow)
   * }}
   *
   * @param name the name of the table reference to return
   */
  def table[T <: Serializable: ClassTag: RowReaderFactory](name: String): CassandraRDD[T] =
    spark.cassandraTable(CassandraKeyspace, name)

  /**
   * Persist data to Cassandra.
   *
   * @param srdd the RDD to use
   *
   * @param tableName the name of the table in Cassandra
   *
   * @param columnNames the optional seq of column names, defaults to Seq.empty
   *
   * @param batchSize the optional batch size, defaults to config
   *
   * @tparam A the data type in the RDD to write
   */
  def write[A: ClassTag](srdd: RDD[A], tableName: String, columnNames: Seq[String] = Seq.empty, batchSize: Option[Int] = None): Unit =
    if (columnNames.isEmpty) srdd.saveToCassandra(CassandraKeyspace, tableName)
    else batchSize orElse CassandraBatchRowSize match {
      case Some(size) ⇒ srdd.saveToCassandra(CassandraKeyspace, tableName, columnNames, size)
      case None       ⇒ srdd.saveToCassandra(CassandraKeyspace, tableName, columnNames)
    }

  /** Returns true if the SparkContext instance has be shutdown. */
  def isTerminated: Boolean = _isTerminated.get

  /** Implicitly shuts down all connections and periodic tasks when the actor system is shutdown. */
  private[spark] def shutdown(): Unit =
    if (_isTerminated.compareAndSet(false, true)) {
      log.info("Shutting down...")
      spark.stop()
      log.info("SparkContext successfully shut down.")
    }
}