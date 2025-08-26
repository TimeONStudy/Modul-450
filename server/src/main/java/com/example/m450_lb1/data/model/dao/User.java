package com.example.m450_lb1.data.model.dao;

import com.example.m450_lb1.data.model.entity.Book;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class User {
    String name;
    String email;

    boolean authenticatedUser;
    List<Book> rentedBooks;
    private Map<Long, LocalDate> lastRentedBooks = new HashMap<>();
}
