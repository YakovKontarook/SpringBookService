package com.spring_web_book.springbookservice.repositories;

import com.spring_web_book.springbookservice.entities.UserBookKey;
import com.spring_web_book.springbookservice.entities.UserBookState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBookStateRepository extends JpaRepository<UserBookState, UserBookKey> {

    Optional<UserBookState> findByUserIdAndBookId(Long userId, Long bookId);
}
