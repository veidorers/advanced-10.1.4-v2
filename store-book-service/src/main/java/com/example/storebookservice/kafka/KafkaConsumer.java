package com.example.storebookservice.kafka;

import com.example.storebookservice.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic-two", groupId = "consumer-group")
    public void listen(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Book book = mapper.readValue(message, Book.class);
            System.out.println("Received message: " + book);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}