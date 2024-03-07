package com.example.storebookservice.service;

import com.example.storebookservice.model.Book;
import com.example.storebookservice.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Book findByName(String name) {
        return bookRepository.findByName(name).orElse(null);
    }
}
