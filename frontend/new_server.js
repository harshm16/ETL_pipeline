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
        clientId: "dthree",
        brokers: ["127.0.0.1:9092"],
    });

    const consumer = kafka.consumer({ groupId: "dthree" });
    await consumer.connect();
    console.log("Consumer connected");

    await consumer.subscribe({
        topic: "randlogs",
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
}

consume();
