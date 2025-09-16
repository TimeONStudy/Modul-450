package com.example.data.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@NotBlank(message = "Book name is required")
	@Size(min = 1, max = 200, message = "Book name must be between 1 and 200 characters")
	@Column(name = "name", nullable = false)
	private String name;

	@NotBlank(message = "Author is required")
	@Size(min = 1, max = 100, message = "Author must be between 1 and 100 characters")
	@Column(name = "author", nullable = false)
	private String author;

	@Column(name = "available", nullable = false)
	private Boolean available = true;

	@Column(name = "rented_date")
	private LocalDateTime rentedDate;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rented_by")
	private User renter;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}