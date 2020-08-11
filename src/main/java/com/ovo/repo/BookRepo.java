package com.ovo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ovo.entity.Book;

public interface BookRepo extends JpaRepository<Book, Long> {
	
	Book findByName(String name);

}
