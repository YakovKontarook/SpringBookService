package com.spring_web_book.springbookservice.security;

import com.spring_web_book.springbookservice.entities.Role;
import com.spring_web_book.springbookservice.entities.User;
import com.spring_web_book.springbookservice.repositories.UserRepository;
import com.spring_web_book.springbookservice.security.jwt.JwtUser;
import com.spring_web_book.springbookservice.security.jwt.JwtUserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDetails loadUserFromToken(String username, Long id, String email, List<String> role) {

        JwtUser jwtUser = JwtUserFactory.create(id, username, role, email);
        return jwtUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user);
        return jwtUser;
    }
}
