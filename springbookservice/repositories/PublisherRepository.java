package com.spring_web_book.springbookservice.repositories;

import com.spring_web_book.springbookservice.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Publisher findByPublisherName(String publisherName);
}
