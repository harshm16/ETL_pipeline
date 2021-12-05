package com.cc


import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.{Dataset, SparkSession}
import org.slf4j

import java.util.regex.Pattern

class LogAnalyzer {

}

/**
 * This object receives a stream of data from kafka topic and aggregates them into windows of pre-defined time
 * interval and counts the number of log messages of each type. Then each window is analyzed for number of messages
 * and if the error or warn type exceed the configured limit within a window, an email is triggered to configured
 * email address. Also the aggregated results are written to a kafka topic that can be used by any other tool.
 */
object LogAnalyzer {
  val logger: slf4j.Logger = CreateLogger(classOf[LogAnalyzer])
  val config: Config = ConfigFactory.load()
  val pattern: Pattern = Pattern.compile(config.getString("spark.log_pattern"))
  val kafkaBroker: String = config.getString("spark.kafka_bootstrap_server")
  val kafkaIncomingTopicName: String = config.getString("spark.kafka_incoming_topic_name")
  val kafkaOutgoingTopicName: String = config.getString("spark.kafka_outgoing_topic_name")

  val TRIGGER_ERROR_MAIL_LIMIT: Int = config.getInt("spark.error_limit")
  val TRIGGER_WARN_MAIL_LIMIT: Int = config.getInt("spark.warn_limit")
  val AGGREGATE_WINDOW_DURATION: String = config.getString("spark.aggregate_window_duration")

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    val spark = SparkSession.builder.appName("Stream Handler").getOrCreate()

    import spark.implicits._

    val inputDF = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("subscribe", kafkaIncomingTopicName)
      .option("failOnDataLoss", false)
      .load()
    spark.sparkContext.setLogLevel("INFO")

    val rawDf = inputDF.selectExpr("CAST(value AS STRING)").as[String]

    val matchDf = rawDf.filter(row => {
      val matcher = pattern.matcher(row.toString)
      matcher.find()
    }).map(row => {
      val matcher = pattern.matcher(row.toString)
      matcher.find()
      (matcher.group(1), matcher.group(2).toString, matcher.group(3).toString, 1)
    }).withColumn("date",
      date_format(col("_1"), "HH:mm:ss.SSS"))
      .withColumnRenamed("_1", "date_string")
      .withColumnRenamed("_2", "msg_type")
      .withColumnRenamed("_3", "msg")
      .withColumnRenamed("_4", "count")

    val encoder = org.apache.spark.sql.Encoders.product[dataframe]
    val matchedDataset: Dataset[dataframe] = matchDf.as(encoder)

    val gb = matchedDataset.groupBy(window(col("date"), AGGREGATE_WINDOW_DURATION).as("time_range"),
      col("msg_type"))
      .count()


    val writeToKafka = gb.select(to_json(struct("*")).as("value"))
      .selectExpr("1", "CAST(value AS STRING) as value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("topic", kafkaOutgoingTopicName)
      .option("checkpointLocation", "/tmp/ch2")
      .option("failOnDataLoss", false)
      .outputMode(OutputMode.Update)
      .start()

    val sendMailDF = gb.writeStream.foreachBatch((df, _) => {
      df.collect().foreach(row => {
        val count = row.get(2).toString.toInt
        row.get(1).toString match {
          case "ERROR" => {
            if (count > TRIGGER_ERROR_MAIL_LIMIT) {
              EmailClient.sendMail("Error", row.get(0).toString, count)
            }
          }
          case "WARN" => {
            if (count > TRIGGER_WARN_MAIL_LIMIT) {
              EmailClient.sendMail("Warn", row.get(0).toString, count)
            }
          }
          case _ => {}
        }
      })
    })
      .outputMode("update")
      .start()

    val query = gb
      .writeStream
      .outputMode("update")
      .format("console")
      .option("truncate", false)
      .option("failOnDataLoss", false)
      .start()

    spark.streams.awaitAnyTermination()

  }

  case class dataframe(date_string: String, msg_type: String, msg: String, count: Int, date: java.sql.Timestamp)
}
