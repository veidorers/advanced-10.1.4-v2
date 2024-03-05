package com.example.kafkaconsumer.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic-one", groupId = "consumer-group")
    public void listen(String message) {
        System.out.println("Received message - " + message);
    }
}
