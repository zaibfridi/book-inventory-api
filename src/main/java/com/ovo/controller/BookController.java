package com.ovo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ovo.entity.Book;
import com.ovo.repo.BookRepo;


@RestController
@RequestMapping("/bookapi")
public class BookController {


    @Autowired
    BookRepo bookRepo;
	
    @RequestMapping("/home")
	  public String helloWorld(){
	    return "Welcome to AWS hosted book inventory api";
	  }
    
	@RequestMapping(value = "/books", method = RequestMethod.GET)
	@ResponseBody 
	public List<Book>  newBookList(Map<String, Object> model) {
	    List<Book> bookList = bookRepo.findAll();
	    return bookList;
	  }
   
	@RequestMapping(value = "/books",method = RequestMethod.POST)
	 public Book create (@RequestBody Book book){
		return bookRepo.save(book);
		 
	 }

		@RequestMapping(value = "/books/{id}",method = RequestMethod.GET)
		public Optional<Book> getBook(@PathVariable long id) {
			return bookRepo.findById(id);
	}	
	
}
