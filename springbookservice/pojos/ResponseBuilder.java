package com.spring_web_book.springbookservice.pojos;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
    private Map<String, Object> jsonObject = new HashMap<>();

    public ResponseBuilder(String message) {
        this.put("msg", message);
    }

    public ResponseBuilder() {
        this("Success");
    }

    public ResponseBuilder put(String key, Object value) {
        jsonObject.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return jsonObject;
    }

    public static Map<String, Object> usernameNotFound(String username) {
        return new ResponseBuilder(String.format("User with username: %s not found", username)).build();
    }

    public static Map<String, Object> jsonIdNotFound(Long id) {
        return new ResponseBuilder(String.format("Book with id %d not found", id)).build();
    }
}
