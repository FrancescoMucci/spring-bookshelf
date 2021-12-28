package io.github.francescomucci.spring.bookshelf.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.francescomucci.spring.bookshelf.model.Book;

@Service
public class TemporaryBookService implements BookService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	@Override
	public List<Book> getAllBooks() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public Book getBookByIsbn(long isbn) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public List<Book> getBooksByTitle(String title) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public Book addNewBook(Book newBook) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public Book replaceBook(Book editedBook) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public void delateBookByIsbn(long isbn) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
