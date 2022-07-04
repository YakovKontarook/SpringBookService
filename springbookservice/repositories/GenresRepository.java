package com.spring_web_book.springbookservice.repositories;

import com.spring_web_book.springbookservice.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface GenresRepository extends JpaRepository<Genre, Long> {
    Set<Genre> findAllByIdIn(Collection<Long> ids);
}
