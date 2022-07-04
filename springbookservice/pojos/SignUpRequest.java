package com.spring_web_book.springbookservice.pojos;

import com.spring_web_book.springbookservice.entities.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class SignUpRequest {
    @NotEmpty(message = "name could not be empty")
    @Size(min = 2, max = 30, message = "incorrect name")
    private String username;
    private String firstName;
    private String lastName;
    @NotEmpty(message = "email could not be empty")
    @Email(message = "invalid email form")
    private String email;
    @Size(min = 8, message = "password should be longer than 8 characters")
    @NotEmpty(message = "password could not be empty")
    private String password;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    public static UserConverter fromUser(User user) {
        UserConverter userConverter = new UserConverter();
        userConverter.setUsername(user.getUsername());
        userConverter.setFirstName(user.getFirstName());
        userConverter.setLastName(user.getLastName());
        userConverter.setEmail(user.getEmail());
        return userConverter;
    }

    public SignUpRequest() {
    }

    public SignUpRequest(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
