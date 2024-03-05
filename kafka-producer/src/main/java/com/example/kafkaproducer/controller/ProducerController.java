package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
    private final KafkaProducer kafkaProducer;

    @Autowired
    public ProducerController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send")
    public String send(@RequestBody String message) {
        kafkaProducer.sendMessage(message);
        return "success";
    }
}
