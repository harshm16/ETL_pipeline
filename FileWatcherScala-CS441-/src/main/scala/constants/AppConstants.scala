package constants

object AppConstants extends Enumeration {
  type String
  val START_MONITORING = "startMonitoring"
  val ACTOR_SYSTEM = "ActorSystem"
}

object KafkaConstants extends Enumeration {
  type String
  val topicName = "cs441"
  val bootstrapServers_k = "bootstrap.servers"
  val bootstrapServers_v = "b-3.cs441-kafka.cxgc19.c10.kafka.us-west-2.amazonaws.com:9092,b-2.cs441-kafka.cxgc19.c10.kafka.us-west-2.amazonaws.com:9092,b-1.cs441-kafka.cxgc19.c10.kafka.us-west-2.amazonaws.com:9092"
  val keySerializer_k = "key.serializer"
  val keySerializer_v = "org.apache.kafka.common.serialization.StringSerializer"
  val valueSerializer_k = "value.serializer"
  val valueSerializer_v = "org.apache.kafka.common.serialization.StringSerializer"
  val acks_k = "acks"
  val acks_v = "all"
}

object ActorConstants extends Enumeration {
  type String
  val extractorSubName = "Extractor_"
  val watcherSubName = "Watcher_"
}

object ShellCommands extends Enumeration {
  type String
  val wc = "wc -l "
  val sed = "sed -n"
}