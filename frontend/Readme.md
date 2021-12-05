# Plot Live graphs from Kafka stream
### This Git repo helps you plot dynamic plots using data from a kafka stream. The data being analysed was taken from the log generator code present in this repo: git@github.com:0x1DOCD00D/LogFileGenerator.git 

## Workflow:
### 1) Subscirbe to Kafka topic using [kafkajs library](https://www.npmjs.com/package/kafkajs).
### 2) String manipulation to get the data in the desired format as the graphs.
### 3) Use [socket io](https://github.com/socketio/socket.io) to send the data from the java script to the html.
### 4) Use [Google Charts](https://developers.google.com/chart/interactive/docs) templates to create graphs.

## To Run:
### Modify Kafka broker ip in the kafka consume code block and the name of the kafka topic you want to subscribe to in the consumer subscribe code block.
### Finally run:
```
node new_server.js
```
### The webpage will be displayed on localhost:6002 by default.

## Kafka 
### [This link](https://kafka.apache.org/quickstart) provides the documentation on everything about Kafka that you will require for this code, including installation, producing & consuming topics from kafka.

## Dependencies:
### The system should have node js & npm installed. All the other dependencies can be installed by running requirements.txt file.

## Some helpful links:
### [Google Charts](https://developers.google.com/chart/interactive/docs)
### [Lenses API](https://docs.lenses.io/2.0/dev/lenses-apis/rest-api/index.html)
### [D3 Js](https://www.d3-graph-gallery.com/graph/custom_theme.html)
### [Kafka streaming workflow](https://www.youtube.com/watch?v=CGT8v8_9i2g)

