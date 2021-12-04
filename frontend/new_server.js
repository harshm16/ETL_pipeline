const { Kafka } = require("kafkajs");

// New Tings
const app = require('express')();
const http = require('http').Server(app);
var io = require('socket.io')(http);
const port = process.env.PORT || 6002


app.get('/', function(req,  res) {
    res.sendFile(__dirname + '/index.html');
  });


  io = io.on('connection', function(socket) {
    console.log('user connected');
    socket.on('disconnect', function() {
      console.log('user disconnected');
    });
  });


http.listen(port, function() {
  console.log('Running on port ' + port);
});


async function consume() {
    const kafka = new Kafka({
        // clientId: "dthree",
        // brokers: ["127.0.0.1:9092"],
        brokers: ["b-3.cs441-kafka.cxgc19.c10.kafka.us-west-2.amazonaws.com:9092","b-2.cs441-kafka.cxgc19.c10.kafka.us-west-2.amazonaws.com:9092","b-1.cs441-kafka.cxgc19.c10.kafka.us-west-2.amazonaws.com:9092"],
    });

    const consumer = kafka.consumer({ groupId: "rand" });
    await consumer.connect();
    console.log("Consumer connected");

    await consumer.subscribe({
        topic: "cs441",
        // topic: "randlo",
        fromBeginning: true,
    });

    var info_count = 0;
    var warn_count = 0;
    var error_count = 0;
    var other_count = 0;
    var total_count = 0;
    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            // 1. topic
            // 2. partition
            // 3. message

          
        var new_d = message.value.toString().split(' ');
        // console.log(value);
        var time_column = new_d[0];
        var log_type = new_d[2];

        total_count = total_count +1
        
        if (log_type == 'INFO') {
            info_count = info_count + 1;
        } 
        else if (log_type == 'ERROR') {
            warn_count = warn_count + 1;
        } 
        else if (log_type == 'WARN') {
            error_count = error_count + 1;
        }
        else{
            other_count = other_count + 1;
        }

        // console.log(info_count, 'info_count');
        // console.log(warn_count, 'warn_count');
        // console.log(error_count, 'error_count');
        // console.log(other_count, 'other_count');

        var a = time_column.split(':'); // split it at the colons

        // minutes are worth 60 seconds. Hours are worth 60 minutes.
        var seconds = (+a[0]) * 60 * 60 + (+a[1]) * 60 + (+a[2]); 
        
        var array_count = [info_count,warn_count,error_count,other_count,time_column,seconds];
        
        console.log(array_count, 'array_count');
        
        io.emit('message', array_count);
        },
    });




    //spark 

    
    const consumer_spark = kafka.consumer({ groupId: "new" });
    await consumer_spark.connect();
    console.log("Consumer connected");

    await consumer_spark.subscribe({
        // topic: "cs441",
        topic: "sparkAnalytics",
        fromBeginning: true,
    });

    var counter = 0
    await consumer_spark.run({
        eachMessage: async ({ topic, partition, message }) => {
            // 1. topic
            // 2. partition
            // 3. message

            var stream_array = message.value.toString().split("'");
      
            spark_array = []
            if (stream_array[13] == 'ERROR'){
    
                var start_time =  Date.parse(stream_array[5]);
                var end_time =  Date.parse(stream_array[9]);
                var type = stream_array[13];
                var count = stream_array[16].split('}')[0].split(' ')[1];
             
                counter = counter + 1
                spark_array = [start_time,end_time,type,count,counter]
        
                console.log(spark_array)
                io.emit('sparkresult', spark_array);
            }

        },
    });


}

consume();
