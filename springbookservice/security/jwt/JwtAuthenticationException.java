package com.spring_web_book.springbookservice.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}