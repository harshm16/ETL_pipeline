# Spark Module
To assemble the code use ```sbt assembly``` in the root directory of the project. The assumptions to run this module are:
1) There is a kafka broker setup.
2) A kafka topic called "cs441" is being written to by the filewatcher module with log messages.
3) The kafka broker IP is updated in the application.conf

Use the command 
```
spark-submit --class com.cc.LogAnalyzer --master "local[*]" --packages "org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.8" target/scala-2.11/spark_app2-assembly-0.1.jar
```
The above command will run the spark application in client mode on a single machine where spark is installed. The spark version this application is tested is 2.4.5.

To run the spark application in cluster mode use the command:
```
spark-submit  --deploy-mode cluster  --class com.cc.LogAnalyzer --master yarn --num-executors 1 --executor-cores 1 --executor-memory 2g  --packages "org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.8" target/scala-2.11/spark_app2-assembly-0.1.jar
```


The project has been deployed and tested on AWS using EMR. 
The spark module's functionality is to process logs in realtime and analyse patterns that emerge over time. This application receives continuous stream of logs from a kafka topic. The logs are then grouped on into time intervals of ```5 seconds``` which is configurable. Log messages of type ```ERROR``` and ```WARN``` are aggregated over the time period and their count is calculated. If any of these counts go beyond a certain threshold, an email is triggered. The threshold for ```ERROR``` messages is ```error_limit``` and that for ```WARN``` is ```warn_limit``` both of which are configurable. The mail address to which the emails have to be triggered is also configurable with parameter ```email_receiver```. The aggregated results are written to another kafka topic called ```sparkAnalytics``` for further analysis or visualizations.

## Description
The object ```LogAnalyzer``` contains the logic to receive data from kafka topic ```cs441``` continuously.

The object ```EmailClient``` sends an email with desired contents.