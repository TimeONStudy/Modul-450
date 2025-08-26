package com.example.m450_lb1.data.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Book {
    @Id
    private Long id;

    private String name;
    private String author;

    private boolean available;
}
