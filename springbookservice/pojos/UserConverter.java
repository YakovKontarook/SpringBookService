package com.spring_web_book.springbookservice.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spring_web_book.springbookservice.entities.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserConverter {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return user;
    }

    public static UserConverter fromUser(User user) {
        UserConverter userConverter = new UserConverter();
        userConverter.setId(user.getId());
        userConverter.setUsername(user.getUsername());
        userConverter.setFirstName(user.getFirstName());
        userConverter.setLastName(user.getLastName());
        userConverter.setEmail(user.getEmail());
        return userConverter;
    }

    public UserConverter() {
    }

    public UserConverter(Long id, String username, String firstName, String lastName, String email) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
