package com.spring_web_book.springbookservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "publisher")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Book> books = new HashSet<>();
    @Column(name = "publisher_name")
    private String publisherName;


    public Publisher() {
    }

    public Publisher(String publisherName) {
        this.publisherName = publisherName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setPublisher(this);
    }

    public Set<Book> getBooks() {
        return books;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String name) {
        this.publisherName = name;
    }
}
