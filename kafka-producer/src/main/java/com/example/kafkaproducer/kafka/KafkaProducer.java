package com.example.kafkaproducer.kafka;

import com.example.kafkaproducer.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Book book)  {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String bookJson = mapper.writeValueAsString(book);
            kafkaTemplate.send("topic-one", bookJson);
            System.out.println("Send message: " + book);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
