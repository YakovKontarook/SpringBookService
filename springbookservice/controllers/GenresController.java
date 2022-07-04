package com.spring_web_book.springbookservice.controllers;

import com.spring_web_book.springbookservice.entities.Genre;
import com.spring_web_book.springbookservice.pojos.ResponseBuilder;
import com.spring_web_book.springbookservice.repositories.BookRepository;
import com.spring_web_book.springbookservice.repositories.GenresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/genres")
public class GenresController {

    BookRepository bookRepository;

    GenresRepository genresRepository;

    @Autowired
    public GenresController(BookRepository bookRepository, GenresRepository genresRepository) {
        this.bookRepository = bookRepository;
        this.genresRepository = genresRepository;
    }

    @PostMapping("/")
    public ResponseEntity<Object> addGenre(@RequestBody Genre genre) {
        genresRepository.save(genre);
        return new ResponseEntity<>(new ResponseBuilder()
                .put("genre", genre).build(), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "limit", defaultValue = "5") int limit) {
        Page<Genre> result = genresRepository.findAll(PageRequest.of(page, limit));
        return new ResponseEntity<>(new ResponseBuilder()
                .put("genres", result.getContent())
                .put("page", result.getNumber())
                .put("pages", result.getTotalPages())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getGenre(@PathVariable Long id) {
        Optional<Genre> genres = genresRepository.findById(id);
        if (genres.isPresent()) {
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("genres", genres.get()).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody Genre newGenre) {
        Optional<Genre> genresOpt = genresRepository.findById(id);
        if (genresOpt.isPresent()) {
            Genre genre = genresOpt.get();
            genre.setName(newGenre.getName());
            genresRepository.save(genre);
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("genre", genre).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Optional<Genre> genreOpt = genresRepository.findById(id);
        if (genreOpt.isPresent()) {
            genresRepository.deleteById(id);
            return new ResponseEntity<>(new ResponseBuilder()
                    .put("publisher", genreOpt.get()).build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseBuilder.jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }
    }
}

