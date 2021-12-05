package com.cc

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.internal.config
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.slf4j.{Logger, LoggerFactory}

import java.util.regex.Pattern

class test1 extends AnyFlatSpec with Matchers {
  val conf: Config = ConfigFactory.load()

  it should "Match the log pattern defined in the conf file" in {
    val example_msg = "23:26:44 [scala-execution-context-global-13] ERROR help - /US|e`bzLKW#8\\W1_:Az'Yc{d~"
    val pattern = Pattern.compile(conf.getString("spark.log_pattern"))
    val matcher = pattern.matcher(example_msg)
    matcher.find() shouldBe(true)
  }

  it should "Match the name of kafka topic at which data from file watcher is being received" in {
    val topic_name = "cs441"
    val define_name= conf.getString("spark.kafka_incoming_topic_name")
    assert(define_name.equals(topic_name))
  }
  it should "Match the name of kafka topic at which data to visualization tool is sent" in {
    val topic_name = "sparkAnalytics"
    val define_name= conf.getString("spark.kafka_incoming_topic_name")
    assert(define_name.equals(topic_name))
  }

  it should "Check if bootstrap server is not empty" in {
    val bootstrap_server = conf.getString("spark.kafka_bootstrap_server")
    assert(bootstrap_server.nonEmpty)
  }

  it should "Check if the email sender account id is one verified by AWS SES" in {
    val sender = conf.getString("spark.email_sender")
    assert(sender.equals("sbharg9@uic.edu"))
  }

}