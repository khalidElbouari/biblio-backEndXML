package com.example.bibliobackend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public class Book {
    private String id;
    private String title;
    private String author;
    private int year;
    private String genre;
    private double price;
    private String publisher;
    private String description;
    private String isbn;
    private String language;
    private int pages;

    // Constructeur
    @JsonCreator
    public Book(String title, String author, int year, String genre, double price,
                String publisher, String description, String isbn, String language, int pages) {
        this.id = UUID.randomUUID().toString(); // Génère un ID unique pour chaque livre
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.price = price;
        this.publisher = publisher;
        this.description = description;
        this.isbn = isbn;
        this.language = language;
        this.pages = pages;
    }
    public Book(String id, String title, String author, int year, String genre, double price,
                String publisher, String description, String isbn, String language, int pages) {
        this.id = id; // On garde l'ID existant
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.price = price;
        this.publisher = publisher;
        this.description = description;
        this.isbn = isbn;
        this.language = language;
        this.pages = pages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters et Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", genre='" + genre + '\'' +
                ", price=" + price +
                ", publisher='" + publisher + '\'' +
                ", description='" + description + '\'' +
                ", isbn='" + isbn + '\'' +
                ", language='" + language + '\'' +
                ", pages=" + pages +
                '}';
    }
}
