package io.github.francescomucci.spring.bookshelf.service;

import java.util.List;

import io.github.francescomucci.spring.bookshelf.model.Book;

public interface BookService {

	public List<Book> getAllBooks();

	public Book getBookByIsbn(long isbn);

	public List<Book> getBooksByTitle(String title);

	public Book addNewBook(Book newBook);

	public Book replaceBook(Book editedBook);

	public void delateBookByIsbn(long isbn);

}
