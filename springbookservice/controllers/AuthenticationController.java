package com.spring_web_book.springbookservice.controllers;

import com.spring_web_book.springbookservice.entities.Role;
import com.spring_web_book.springbookservice.entities.User;
import com.spring_web_book.springbookservice.pojos.AuthenticationRequest;
import com.spring_web_book.springbookservice.pojos.ResponseBuilder;
import com.spring_web_book.springbookservice.pojos.SignUpRequest;
import com.spring_web_book.springbookservice.pojos.Status;
import com.spring_web_book.springbookservice.repositories.RoleRepository;
import com.spring_web_book.springbookservice.repositories.UserRepository;
import com.spring_web_book.springbookservice.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private UserRepository userRepository;

    private final RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                    UserRepository userRepository,
                                    RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authRequest) {
        try {
            String username = authRequest.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
            User user = userRepository.findByUsername(username);

            if (user == null) {
                return new ResponseEntity<>(ResponseBuilder
                        .usernameNotFound(username), HttpStatus.NOT_FOUND);
            }

            String token = jwtTokenProvider.createToken(username, user.getRoles(), user.getId(), user.getEmail());

            return new ResponseEntity<>(new ResponseBuilder()
                    .put("token", token)
                    .build(), HttpStatus.OK);

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new ResponseBuilder(
                    "Invalid username or password").build(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody @Valid SignUpRequest signUpRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ResponseBuilder("Something went wrong")
                    .put("Error", bindingResult.getFieldError().getDefaultMessage())
                    .build(), HttpStatus.NOT_ACCEPTABLE);
        }

        User user = signUpRequest.toUser();

        Role userRole = roleRepository.findByName("ROLE_USER");
        user.addRole(userRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.ACTIVE);

        try {
            User registeredUser = userRepository.save(user);
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("User", registeredUser)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseBuilder(
                    "User with this email or username already exists").build(), HttpStatus.CONFLICT);
        }
    }
}



