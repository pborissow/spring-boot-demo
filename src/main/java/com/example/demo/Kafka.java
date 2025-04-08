package com.example.demo;
import com.example.demo.persistance.*;

import java.util.*;
import java.time.Duration;
import javaxt.json.JSONObject;

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
    private RequestRepository requestRepository;


  //**************************************************************************
  //** Constructor
  //**************************************************************************
    public Kafka(String host, String topic, RequestRepository requestRepository) {
        this.host = host;
        this.topic = topic;
        this.requestRepository = requestRepository;
    }


  //**************************************************************************
  //** start
  //**************************************************************************
    public static void start(){
        JSONObject kafkaConfig = Config.get("kafka").toJSONObject();
        if (kafkaConfig==null){
            System.out.println("Missing kafka config");
            return;
        }
        String kafkaHost = kafkaConfig.get("host").toString();
        String kafkaTopic = kafkaConfig.get("topic").toString();
        RequestRepository requestRepository = (RequestRepository) Config.get("requestRepository").toObject();
        new Thread(new Kafka(kafkaHost, kafkaTopic, requestRepository)).start();
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

        RequestEntity httpRequest = new RequestEntity();
        httpRequest.setDate(date.getDate());
        httpRequest.setDomain(domain);
        httpRequest.setPath(path);

        requestRepository.save(httpRequest);
    }

}