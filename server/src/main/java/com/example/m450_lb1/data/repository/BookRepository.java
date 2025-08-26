package com.example.m450_lb1.data.repository;

import com.example.m450_lb1.data.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBookByName(String bookName);
}
