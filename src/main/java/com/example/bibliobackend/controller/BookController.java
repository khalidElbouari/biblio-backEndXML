package com.example.bibliobackend.controller;

import com.example.bibliobackend.model.Book;
import com.example.bibliobackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Autoriser les requêtes depuis React
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // Endpoint pour ajouter un livre
    @PostMapping
    public String addBook(@RequestBody Book book) {
        try {
            bookService.addBook(book);
            return "Book added successfully!";
        } catch (Exception e) {
            return "Error adding book: " + e.getMessage();
        }
    }

    // Endpoint pour récupérer tous les livres
    @GetMapping
    public List<Book> getBooks() {
        try {
            return bookService.getAllBooks();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving books: " + e.getMessage());
        }
    }
}