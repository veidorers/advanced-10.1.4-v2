package com.example.storebookservice.kafka;

import com.example.storebookservice.model.Book;
import com.example.storebookservice.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final BookService bookService;

    public KafkaConsumer(BookService bookService) {
        this.bookService = bookService;
    }

    @KafkaListener(topics = "topic-two", groupId = "consumer-group")
    public void listen(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Book book = mapper.readValue(message, Book.class);
            System.out.println("Received message: " + book.getName());
            bookService.save(book);
            System.out.println("Saved book: " + book.getName());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
