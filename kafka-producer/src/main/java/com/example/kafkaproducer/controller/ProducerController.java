package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.kafka.KafkaProducer;
import com.example.kafkaproducer.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
    private final KafkaProducer kafkaProducer;

    @Autowired
    public ProducerController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send")
    public String send() {
        var book = new Book();
        book.setName("Book" + book.hashCode());
        book.setDescription("Description" + book.hashCode());
        book.setChecked(false);
        book.setId(null);
        kafkaProducer.sendMessage(book);
        return "success";
    }
}
