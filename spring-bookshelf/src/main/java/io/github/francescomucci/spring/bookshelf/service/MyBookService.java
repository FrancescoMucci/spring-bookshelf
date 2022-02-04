package io.github.francescomucci.spring.bookshelf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;
import io.github.francescomucci.spring.bookshelf.exception.BookAlreadyExistException;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;

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
		return bookRepository.findById(isbn)
			.orElseThrow(() -> new BookNotFoundException(isbn));
	}

	@Override
	public List<Book> getBooksByTitle(String title) {
		List<Book> bookListWithTitle = bookRepository.findAllByTitleLikeOrderByTitle(title);
		if (bookListWithTitle.isEmpty())
			throw new BookNotFoundException(title);
		return bookListWithTitle;
	}

	@Override
	public Book addNewBook(Book newBook) {
		checkBookNonExistenceByIsbn(newBook.getIsbn());
		return bookRepository.save(newBook);
	}

	@Override
	public Book replaceBook(Book editedBook) {
		checkBookExistenceByIsbn(editedBook.getIsbn());
		return bookRepository.save(editedBook);
	}

	@Override
	public void delateBookByIsbn(long isbn) {
		checkBookExistenceByIsbn(isbn);
		bookRepository.deleteById(isbn);
	}

	private void checkBookExistenceByIsbn(long isbn) {
		if (!bookRepository.existsById(isbn))
			throw new BookNotFoundException(isbn);
	}

	private void checkBookNonExistenceByIsbn(long isbn) {
		if (bookRepository.existsById(isbn))
			throw new BookAlreadyExistException(isbn);
	}

}
