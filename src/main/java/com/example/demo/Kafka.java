package com.example.demo;

import java.util.*;
import java.time.Duration;
import static javaxt.utils.Console.console;

//Kafka Stuff
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;


//******************************************************************************
//**  Kafka Consumer
//******************************************************************************
/**
 *   Used to retrieve and persist messages from a Kafka server
 *
 ******************************************************************************/

public class Kafka implements Runnable {

    private String host;
    private String topic;


  //**************************************************************************
  //** Constructor
  //**************************************************************************
    public Kafka(String host, String topic) {
        this.host = host;
        this.topic = topic;
    }


  //**************************************************************************
  //** run
  //**************************************************************************
    public void run() {


        Properties props = new Properties();
        props.put("bootstrap.servers", host);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("group.id", "demo"); //Required! Use whatever


        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)){


          //Check if topic exists
            boolean foundTopic = false;
            for (String t : consumer.listTopics().keySet()){
                if (t.equalsIgnoreCase(topic)){
                    topic = t;
                    foundTopic = true;
                }
            }
            if (!foundTopic) throw new RuntimeException("topic " + topic + " not found");


          //Subscribe to the topic
            consumer.subscribe(Collections.singletonList(topic));


          //Poll for messages
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    String key = record.key();
                    String value = record.value();
                    Integer partition = record.partition();
                    Long offset = record.offset();

                    try{
                        saveHttpRequest(record);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
        }
    }


  //**************************************************************************
  //** saveHttpRequest
  //**************************************************************************
    private void saveHttpRequest(ConsumerRecord<String, String> record) throws Exception {

        String[] request = record.value().split("\t");

        javaxt.utils.Date date = new javaxt.utils.Date(request[0]);
        String domain = request[1];
        String path = request[2];
        String clientIP = request[3];
        String userAgent = request[4];

        Integer partition = record.partition();
        Long offset = record.offset();

    }

}