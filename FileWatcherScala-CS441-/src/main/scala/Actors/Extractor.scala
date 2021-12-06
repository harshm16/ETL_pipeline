package Actors

import akka.actor.{Actor, Props}
import constants.KafkaConstants
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import service.ExtractorService

import java.io.File
import java.util.Properties
import java.util.logging.{Level, Logger}

object Extractor {
  def props(extractorService: ExtractorService, file: File): Props = Props(new Extractor(extractorService, file))
}

class Extractor(extractorService: ExtractorService, file: File) extends Actor {
  val props: Properties = new Properties()
  props.put(KafkaConstants.bootstrapServers_k,  KafkaConstants.bootstrapServers_v)
  props.put(KafkaConstants.keySerializer_k,     KafkaConstants.keySerializer_v)
  props.put(KafkaConstants.valueSerializer_k,   KafkaConstants.valueSerializer_v)
  props.put(KafkaConstants.acks_k,              KafkaConstants.acks_v)

  val producer = new KafkaProducer[String, String](props)
  val logger: Logger = Logger.getLogger(this.getClass.getName)
  val topic: String = KafkaConstants.topicName

  override def receive: Receive = {
    case str: String =>
      logger.log(Level.FINE, "Extracting for file: \t" + self.path.name)
      extractorService.getData(str, file).foreach{line =>
        try {
          val record = new ProducerRecord(topic, "key", line)
          logger.log(Level.FINEST, line)
          producer.send(record)
        }
        catch {
          case e: Exception => e.printStackTrace()
        }
      }
  }
}
