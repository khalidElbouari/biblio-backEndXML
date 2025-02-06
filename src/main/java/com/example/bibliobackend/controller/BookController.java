package com.example.bibliobackend.controller;

import com.example.bibliobackend.model.Book;
import com.example.bibliobackend.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    @Autowired
    private BookService bookService;

    // Endpoint pour récupérer tous les livres
    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            // Retourne une erreur si la récupération échoue
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addBook(@RequestBody Book book) {
        try {
                bookService.addBook(book);
                return new ResponseEntity<>(Map.of("message", "Book added successfully!"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error adding book: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/after-year/{year}")
    public ResponseEntity<List<Book>> getBooksAfterYear(@PathVariable int year) {
        try {
            List<Book> books = bookService.getBooksAfterYear(year);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error filtering books by year", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint pour filtrer les livres par auteur
    @GetMapping("/by-author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        try {
            List<Book> books = bookService.getBooksByAuthor(author);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint pour supprimer un livre
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable String bookId) {
        try {
            // Appel au service pour supprimer le livre en fonction de son ID
            boolean isDeleted = bookService.deleteBook(bookId);
            if (isDeleted) {
                return ResponseEntity.ok(Map.of("message", "Livre supprimé avec succès !"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Livre non trouvé"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression du livre: " + e.getMessage()));
        }
    }

}
