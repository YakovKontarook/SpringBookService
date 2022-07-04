package com.spring_web_book.springbookservice.configs;

import com.spring_web_book.springbookservice.security.jwt.JwtConfigurer;
import com.spring_web_book.springbookservice.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String LOGIN_ENDPOINT = "/api/v1/auth/**";
    private static final String BOOK_ENDPOINT = "/api/v1/books/**";
    private static final String GENRE_ENDPOINT = "/api/v1/books/genres/**";
    private static final String PUBLISHER_ENDPOINT = "/api/v1/books/publishers/**";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers("/api/v1/books/**/like").hasAnyRole("USER", "STUFF", "ADMIN")
                .antMatchers(HttpMethod.GET, BOOK_ENDPOINT, GENRE_ENDPOINT, PUBLISHER_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.PUT, BOOK_ENDPOINT, GENRE_ENDPOINT, PUBLISHER_ENDPOINT).hasAnyRole("STUFF", "ADMIN")
                .antMatchers(HttpMethod.POST, BOOK_ENDPOINT, GENRE_ENDPOINT, PUBLISHER_ENDPOINT).hasAnyRole("STUFF", "ADMIN")
                .antMatchers(HttpMethod.DELETE, BOOK_ENDPOINT, GENRE_ENDPOINT, PUBLISHER_ENDPOINT).hasRole("ADMIN")

                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

}
