package com.spring_web_book.springbookservice.controllers;

import com.spring_web_book.springbookservice.entities.Book;
import com.spring_web_book.springbookservice.entities.Publisher;
import com.spring_web_book.springbookservice.pojos.ResponseBuilder;
import com.spring_web_book.springbookservice.repositories.BookRepository;
import com.spring_web_book.springbookservice.repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/publishers")
public class PublisherController {

    private final PublisherRepository publisherRepository;

    private final BookRepository bookRepository;

    @Autowired
    public PublisherController(PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/")
    public ResponseEntity<Object> addPublisher(@RequestBody Publisher publisher) {
        publisherRepository.save(publisher);
        return new ResponseEntity<>(new ResponseBuilder()
                .put("publisher", publisher).build(), HttpStatus.OK);
    }

    @PostMapping("/{id}/books")
    public ResponseEntity<Object> addBook(@RequestBody Book book, @PathVariable Long id) {
        Optional<Publisher> publisher = publisherRepository.findById(id);
        if (publisher.isPresent()) {
            book.setPublisher(publisher.get());
            bookRepository.save(book);
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("book", book).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "limit", defaultValue = "5") int limit) {
        Page<Publisher> result = publisherRepository.findAll(PageRequest.of(page, limit));
        return new ResponseEntity<>(new ResponseBuilder()
                .put("publishers", result.getContent())
                .put("page", result.getNumber())
                .put("pages", result.getTotalPages())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPublisher(@PathVariable Long id) {
        Optional<Publisher> publisher = publisherRepository.findById(id);
        if (publisher.isPresent()) {
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("publisher", publisher.get()).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody Publisher newPublisher) {
        Optional<Publisher> publisherOpt = publisherRepository.findById(id);
        if (publisherOpt.isPresent()) {
            Publisher publisher = publisherOpt.get();
            publisher.setPublisherName(newPublisher.getPublisherName());
            publisherRepository.save(publisher);
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("publisher", publisher).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Optional<Publisher> publisherOpt = publisherRepository.findById(id);
        if (publisherOpt.isPresent()) {
            publisherRepository.deleteById(id);
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("publisher", publisherOpt.get()).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }
}



