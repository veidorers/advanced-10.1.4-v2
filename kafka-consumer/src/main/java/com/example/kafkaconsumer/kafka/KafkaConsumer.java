package com.example.kafkaconsumer.kafka;

import com.example.kafkaconsumer.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final KafkaProducer kafkaProducer;

    @Autowired
    public KafkaConsumer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }


    @KafkaListener(topics = "topic-one", groupId = "consumer-group")
    public void listen(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Book book = mapper.readValue(message, Book.class);
            System.out.println("Received message: " + book);
            book.setChecked(true);
            kafkaProducer.sendMessage(book);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
