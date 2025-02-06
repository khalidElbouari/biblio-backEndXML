package com.example.bibliobackend.service;

import com.example.bibliobackend.model.Book;
import com.example.bibliobackend.util.XMLValidator;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    @Value("${library.xml.path}")
    private String xmlFilePath;
    private final XMLValidator xmlValidator;

    @Autowired
    public BookService(XMLValidator xmlValidator) {
        this.xmlValidator = xmlValidator;
    }

    public List<Book> getAllBooks() throws Exception {
        File xmlFile = new File(xmlFilePath);
        Document document = parseDocument(xmlFile);
        NodeList nodeList = document.getElementsByTagName("book");
        return extractBooksFromNodeList(nodeList);
    }

    public void addBook(Book book) throws Exception {
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            throw new FileNotFoundException("Le fichier XML est introuvable.");
        }

        boolean isValid = xmlValidator.validateXML(xmlFilePath, "src/main/resources/XMLData/books.xsd");
        if (isValid) {
            Document document = parseDocument(xmlFile);
            Element newBook = document.createElement("book");

            appendChildElement(newBook, "id", book.getId(), document);
            appendChildElement(newBook, "title", book.getTitle(), document);
            appendChildElement(newBook, "author", book.getAuthor(), document);
            appendChildElement(newBook, "year", String.valueOf(book.getYear()), document);
            appendChildElement(newBook, "price", String.valueOf(book.getPrice()), document);
            appendChildElement(newBook, "description", book.getDescription(), document);

            NodeList nodeList = document.getElementsByTagName("library");
            Element library = (Element) nodeList.item(0);
            library.appendChild(newBook);

            transformDocumentToFile(document, xmlFile);
        }else{
            throw new Exception("Le fichier XML est invalide !");

        }
    }

    private Document parseDocument(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);
    }

    private void appendChildElement(Element parent, String tagName, String value, Document document) {
        Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(value));
        parent.appendChild(element);
    }

    private void transformDocumentToFile(Document document, File xmlFile) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }

    private List<Book> extractBooksFromNodeList(NodeList nodeList) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element bookElement = (Element) node;
                books.add(new Book(
                       getTagValue(bookElement, "id"),
                        getTagValue(bookElement, "title"),
                        getTagValue(bookElement, "author"),
                        Integer.parseInt(getTagValue(bookElement, "year")),
                        getTagValue(bookElement, "genre"),
                        parseDouble(getTagValue(bookElement, "price")),
                        getTagValue(bookElement, "publisher"),
                        getTagValue(bookElement, "description"),
                        getTagValue(bookElement, "isbn"),
                        getTagValue(bookElement, "language"),
                        parseInt(getTagValue(bookElement, "pages"))
                ));
            }
        }
        return books;
    }

    private double parseDouble(String value) {
        return value.isEmpty() ? 0.0 : Double.parseDouble(value);
    }

    private int parseInt(String value) {
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    private String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
    }

    // --- Méthodes utilisant XQuery (pour filtrer les livres) ---
    // Pour filtrer par année, par exemple
    public List<Book> getBooksAfterYear(int year) throws Exception {
        Resource resource = new ClassPathResource("XMLData/library.xml");
        File xmlFile = resource.getFile();
        String absoluteUri = xmlFile.toURI().toString();

        String xquery = String.format(
                "for $book in doc('%s')/library/book where xs:integer($book/year) > %d return $book",
                absoluteUri, year
        );
        return executeXQuery(xquery);
    }

    // Pour filtrer par auteur
    public List<Book> getBooksByAuthor(String author) throws Exception {
        Resource resource = new ClassPathResource("XMLData/library.xml");
        File xmlFile = resource.getFile();
        String absoluteUri = xmlFile.toURI().toString();

        String validAuthor = author.trim().toLowerCase();
        String xquery = String.format(
                "for $book in doc('%s')/library/book where lower-case($book/author) = '%s' return $book",
                absoluteUri, validAuthor
        );
        return executeXQuery(xquery);
    }

    // Exécution de l'XQuery et extraction des résultats
    private List<Book> executeXQuery(String xquery) throws Exception {
        Processor processor = new Processor(false);
        XQueryCompiler compiler = processor.newXQueryCompiler();
        XQueryExecutable exec = compiler.compile(xquery);
        XQueryEvaluator evaluator = exec.load();

        // Exécutez la requête et récupérez le résultat
        XdmValue result = evaluator.evaluate();

        List<Book> books = new ArrayList<>();
        for (XdmItem item : result) {
            if (item instanceof XdmNode) {
                XdmNode node = (XdmNode) item;
                // si le nœud est un élément et que getNodeName() n'est pas null
                if (node.getNodeKind() == XdmNodeKind.ELEMENT &&
                        node.getNodeName() != null &&
                        "book".equals(node.getNodeName().getLocalName())) {

                    books.add(new Book(
                            getTagValue(node, "title"),
                            getTagValue(node, "author"),
                            Integer.parseInt(getTagValue(node, "year")),
                            getTagValue(node, "genre"),
                            parseDouble(getTagValue(node, "price")),
                            getTagValue(node, "publisher"),
                            getTagValue(node, "description"),
                            getTagValue(node, "isbn"),
                            getTagValue(node, "language"),
                            parseInt(getTagValue(node, "pages"))
                    ));
                }
            }
        }
        return books;
    }

    // Méthode utilitaire pour extraire la valeur d'un tag depuis un XdmNode
    private String getTagValue(XdmNode node, String tagName) {
        XdmSequenceIterator iterator = node.axisIterator(Axis.CHILD);
        while (iterator.hasNext()) {
            XdmNode child = (XdmNode) iterator.next();
            if (child.getNodeKind() == XdmNodeKind.ELEMENT && child.getNodeName() != null) {
                if (child.getNodeName().getLocalName().equals(tagName)) {
                    return child.getStringValue();
                }
            }
        }
        return "";
    }
    public boolean deleteBook(String bookId) throws Exception {
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            throw new FileNotFoundException("Le fichier XML est introuvable.");
        }

        // Charger le fichier XML
        Document document = parseDocument(xmlFile);
        NodeList nodeList = document.getElementsByTagName("book");

        // Parcourir les livres pour trouver celui qui correspond à l'ID
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element bookElement = (Element) node;
                String bookIdStr = getTagValue(bookElement, "id");  // Récupère l'ID du livre
                if (bookIdStr != null && bookIdStr.equals(bookId)) {  // Compare avec l'ID passé en paramètre
                    // Supprimer le livre
                    node.getParentNode().removeChild(node);
                    transformDocumentToFile(document, xmlFile);  // Sauvegarder les changements
                    return true;
                }
            }
        }
        return false;  // Livre non trouvé
    }

}
