package com.spring_web_book.springbookservice.security.jwt;

import com.spring_web_book.springbookservice.entities.Role;
import com.spring_web_book.springbookservice.entities.User;
import com.spring_web_book.springbookservice.pojos.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class JwtUserFactory {
    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getRoles()),
                user.getStatus().equals(Status.ACTIVE),
                user.getUpdatedTimestamp()
        );
    }

    public static JwtUser create(Long id, String username, List<String> roles, String email) {
        return new JwtUser(
                id, username, email,
                mapToGrantedAuthorities(roles)
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Iterable<?> userRoles) {
        return StreamSupport.stream(userRoles.spliterator(), false)
                .map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
    }

//    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
//        return userRoles.stream()
//                .map(role ->
//                        new SimpleGrantedAuthority(role.getName())
//                ).collect(Collectors.toList());
//    }
}
