package com.spring_web_book.springbookservice.controllers;


import com.spring_web_book.springbookservice.entities.*;
import com.spring_web_book.springbookservice.pojos.LikeRequest;
import com.spring_web_book.springbookservice.pojos.ResponseBuilder;
import com.spring_web_book.springbookservice.repositories.*;
import com.spring_web_book.springbookservice.security.jwt.JwtTokenProvider;
import com.spring_web_book.springbookservice.security.jwt.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.spring_web_book.springbookservice.entities.Book.sortByList;

@RestController
@RequestMapping("/api/v1/books")

public class BookController {

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final GenresRepository genresRepository;

    private final UserRepository userRepository;

    private final UserBookStateRepository userBookStateRepository;


    @Autowired
    public BookController(BookRepository bookRepository, PublisherRepository publisherRepository,
                          GenresRepository genresRepository, UserRepository userRepository,
                          UserBookStateRepository userBookStateRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.genresRepository = genresRepository;
        this.userRepository = userRepository;
        this.userBookStateRepository = userBookStateRepository;
    }

    @PostMapping("/")
    public ResponseEntity<Object> add(@RequestBody Book book) {
        Optional<Publisher> publisher = publisherRepository.findById(book.getPublisherId());
        if (publisher.isEmpty()) {
            return new ResponseEntity<>(ResponseBuilder
                    .jsonIdNotFound(book.getPublisherId()), HttpStatus.NOT_FOUND);
        }
        book.setPublisher(publisher.get());
        Set<Genre> genres = genresRepository.findAllByIdIn(book.getGenreIds());
        book.setGenres(genres);
        bookRepository.save(book);
        return new ResponseEntity<>(new ResponseBuilder()
                .put("book", book).build(), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "5") int limit,
            @RequestParam(name = "max-price", required = false) Double maxPrice,
            @RequestParam(name = "search-query", required = false) String searchQuery,
            @RequestParam(name = "publisher-id", required = false) Long publisherId,
            @RequestParam(name = "sort-by", defaultValue = "releaseDate") String sortBy,
            @RequestParam(name = "sort-order", defaultValue = "asc") String sortOrder,
            @RequestParam(name = "genre-ids", required = false) List<Long> genreIds,
            @RequestParam(name = "book-id", required = false) Long bookId,
            Authentication authentication
    ) {
        Sort.Direction direction = sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (!sortByList.contains(sortBy)) {
            sortBy = "releaseDate";
        }
        Long userId = null;
        if (authentication != null) {
            JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
            userId = jwtUser.getId();
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sortBy));
        List<Book> books = bookRepository.findAllFiltered(
                maxPrice, searchQuery, publisherId, genreIds, userId, bookId, pageable
        );
        long pageCount = bookRepository.countPages(
                maxPrice, searchQuery, publisherId, genreIds, userId, bookId, pageable
        );

        return new ResponseEntity<>(new ResponseBuilder()
                .put("books", books)
                .put("page", page)
                .put("pageCount", pageCount)
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable Long id,
                                      Authentication authentication
    ) {
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        Long userId = jwtUser.getId();

        List<Book> books = bookRepository.findAllFiltered(
                null, null, null, null, userId, id, null
        );

        if (books.isEmpty()) {
            return new ResponseEntity<>(ResponseBuilder
                    .jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new ResponseBuilder()
                .put("book", books.get(0))
                .build(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody Book newBook
    ) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isEmpty()) {
            return new ResponseEntity<>(ResponseBuilder
                    .jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }

        Book book = bookOpt.get();
        book.setTitle(newBook.getTitle());
        book.setDescription(newBook.getDescription());
        book.setPrice(newBook.getPrice());
        book.setReleaseDate(newBook.getReleaseDate());
        Set<Genre> genres = genresRepository.findAllByIdIn(book.getGenreIds());
        book.setGenres(genres);
        bookRepository.save(book);
        return new ResponseEntity<>(new ResponseBuilder()
                .put("book", book).build(), HttpStatus.OK);

    }

    @PostMapping("/{id}/like")
    @Transactional
    public ResponseEntity<Object> updateLike(@PathVariable(name = "id") Long bookId,
                                             @RequestBody LikeRequest likeRequest,
                                             Authentication authentication

    ) {

        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        Long userId = jwtUser.getId();

        Optional<Book> bookOpt = bookRepository.findWithUserState(bookId, userId);
        if (bookOpt.isEmpty()) {
            return new ResponseEntity<>(ResponseBuilder
                    .jsonIdNotFound(bookId), HttpStatus.NOT_FOUND);
        }
        Book book = bookOpt.get();

        if (book.getUserState() == null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new ResponseEntity<>(ResponseBuilder
                        .jsonIdNotFound(userId), HttpStatus.NOT_FOUND);
            }

            User user = userOpt.get();
            book.setUserState(new UserBookState(user, book));
        }

        if (book.getUserState().isLiked() != likeRequest.isLiked()) {
            book.getUserState().setLiked(likeRequest.isLiked());
            userBookStateRepository.save(book.getUserState());
            bookRepository.save(book);
            bookRepository.addLikeCount(bookId, likeRequest.isLiked() ? 1L : -1L);
            Optional<Long> likeCount = bookRepository.getLikeCount(bookId);
            if (likeCount.isPresent()) {
                book.setLikeCount(likeCount.get());
            }
        }
        return new ResponseEntity<>(new ResponseBuilder()
                .put("book", book).build(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isEmpty()) {
            return new ResponseEntity<>(ResponseBuilder
                    .jsonIdNotFound(id), HttpStatus.NOT_FOUND);
        }

        bookRepository.deleteById(id);
        return new ResponseEntity<>(new ResponseBuilder()
                .put("book", bookOpt).build(), HttpStatus.OK);
    }
}
