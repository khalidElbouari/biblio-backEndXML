package com.example.bibliobackend.service;


import com.example.bibliobackend.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class BookService {

    @Value("${library.xml.path}")
    private String xmlFilePath;

    public void addBook(Book book) throws Exception {
        // Utiliser le chemin configuré
        Resource resource = new ClassPathResource(xmlFilePath);
        File xmlFile = resource.getFile();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        Element newBook = document.createElement("book");

        Element title = document.createElement("title");
        title.appendChild(document.createTextNode(book.getTitle()));
        newBook.appendChild(title);

        Element author = document.createElement("author");
        author.appendChild(document.createTextNode(book.getAuthor()));
        newBook.appendChild(author);

        Element year = document.createElement("year");
        year.appendChild(document.createTextNode(String.valueOf(book.getYear())));
        newBook.appendChild(year);

        NodeList nodeList = document.getElementsByTagName("library");
        Element library = (Element) nodeList.item(0);
        library.appendChild(newBook);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }

    public List<Book> getAllBooks() throws Exception {
        List<Book> books = new ArrayList<>();

        // Utiliser le chemin configuré
        Resource resource = new ClassPathResource(xmlFilePath);
        File xmlFile = resource.getFile();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("book");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element bookElement = (Element) node;
                String title = bookElement.getElementsByTagName("title").item(0).getTextContent();
                String author = bookElement.getElementsByTagName("author").item(0).getTextContent();
                int year = Integer.parseInt(bookElement.getElementsByTagName("year").item(0).getTextContent());
                books.add(new Book(title, author, year));
            }
        }
        return books;
    }
}