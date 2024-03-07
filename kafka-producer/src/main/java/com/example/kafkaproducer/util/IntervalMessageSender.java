package com.example.kafkaproducer.util;

import com.example.kafkaproducer.kafka.KafkaProducer;
import com.example.kafkaproducer.model.Book;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class IntervalMessageSender {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final KafkaProducer kafkaProducer;

    @Autowired
    public IntervalMessageSender(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostConstruct
    public void startSendingMessages() {
        scheduler.scheduleAtFixedRate(this::sendMessage, 0, 10, TimeUnit.SECONDS);
    }

    public void sendMessage() {
        var book = new Book();
        var random = new Random();
        book.setName(String.valueOf(random.nextInt(1000000)));
        var hashCode = book.hashCode();
        book.setName("Book" + hashCode);
        book.setDescription("Description" + hashCode);
        book.setChecked(false);
        book.setId(null);
        kafkaProducer.sendMessage(book);
    }


}
