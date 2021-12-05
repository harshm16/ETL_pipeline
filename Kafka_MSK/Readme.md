# **_[Amazon Managed Streaming for Apache Kafka(Amazon MSK)](https://aws.amazon.com/msk/)_*
![alt text](https://d1.awsstatic.com/reInvent/re21-pdp-tier1/amazon-msk/product-page-diagram_MSK-How-It-Works-20211111.a30e3b058be45a2c58c36fe02acf48be4ba291c0.png)

We can capture events with MSK and this will be leveraged to stream the logs. Detailed implementation of the MSK is shown in the video.

### Commands used to execute MSK:

Install Java:

`sudo yum install java-1.8.0`

Kafka client is needed for running kafka producer and kafka console consumer command scripts.
Download Kafka:

`wget https://archive.apache.org/dist/kafka/2.1.0/kafka_2.11-2.1.0.tgz`

Extract Zip File to a folder:

`tar -xzf kafka_2.11-2.1.0.tgz`

Getting BootStrap brokers:

`aws kafka get-bootstrap-brokers --cluster-arn "ClusterArn" --region`

Logging into EMR cluster with secret key provided during cluster creation:

`aws kafka describe-cluster --cluster-arn "ClusterArn" --region`

Creating Kafka Topic:

`bin/kafka-topics.sh --create --zookeeper ZooKeeperInstance --replication-factor 2 --partitions 1 TopicName`

Producer:

`bin/kafka-console-producer.sh --broker-list BootstrapBrokerString --topic TopicName`

Consumer:

`bin/kafka-console-consumer.sh --bootstrap-server BootstrapBrokerString --topic TopicName --from-beginning`

### Key Takeaway/Conclusion:

Now that we can use kafka as a Managed Service on AWS, it helps us focus more on processing layer to consume data from MSK and further provide it to the visualization layers.
