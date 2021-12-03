package io.github.francescomucci.spring.bookshelf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;

@Service("BookService")
public class MyBookService implements BookService {

	private BookRepository bookRepository;

	@Autowired
	public MyBookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public List<Book> getAllBooks() {
		return bookRepository.findAll(Sort.by("title"));
	}

	@Override
	public Book getBookByIsbn(long isbn) {
		return null;
	}

	@Override
	public List<Book> getBooksByTitle(String title) {
		return null;
	}

	@Override
	public Book addNewBook(Book newBook) {
		return null;
	}

	@Override
	public Book replaceBook(Book editedBook) {
		return null;
	}

	@Override
	public void delateBookByIsbn(long isbn) {
		
	}

}
