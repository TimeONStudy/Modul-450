package com.example.data.repository;

import com.example.data.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

	List<Book> findByCategoryId(String categoryId);

	List<Book> findByRenterId(String userId);

	List<Book> findByCategoryIdAndAvailable(String categoryId, boolean available);

	@Query("SELECT b FROM Book b WHERE b.renter.id = :userId")
	List<Book> findRentedBooksByUserId(@Param("userId") String userId);

	@Query("SELECT COUNT(b) FROM Book b WHERE b.renter.id = :userId AND b.category.id = :categoryId")
	int countRentedBooksByUserAndCategory(@Param("userId") String userId, @Param("categoryId") String categoryId);
}
