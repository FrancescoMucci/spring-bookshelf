package io.github.francescomucci.spring.bookshelf.exception;

public class BookNotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public static final String BOOK_NOT_FOUND_MSG = ": no book found with this ISBN-13";

	public BookNotFoundException(long isbn) {
		super(isbn + BOOK_NOT_FOUND_MSG);
	}

}
