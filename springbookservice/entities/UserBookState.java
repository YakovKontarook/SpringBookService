package com.spring_web_book.springbookservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "user_book_states")
public class UserBookState {

    @EmbeddedId
    @JsonIgnore
    private UserBookKey id = new UserBookKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    @JsonBackReference
    private Book book;

    private boolean liked;

    public UserBookState() {
    }

    public UserBookState(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    public UserBookState(User user, Book book, boolean liked) {
        this.user = user;
        this.book = book;
        this.liked = liked;
    }

    public UserBookKey getId() {
        return id;
    }

    public void setId(UserBookKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

}
