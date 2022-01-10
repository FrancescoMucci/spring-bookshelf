package io.github.francescomucci.spring.bookshelf.exception;

public class BookNotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public static final String BOOK_NOT_FOUND_MSG = ": no book found with this ISBN-13";

	public static final String BOOK_NOT_FOUND_TITLE_MSG = ": no book found with this title";

	public BookNotFoundException(long isbn) {
		super(isbn + BOOK_NOT_FOUND_MSG);
	}

	public BookNotFoundException(String title) {
		super(title + BOOK_NOT_FOUND_TITLE_MSG);
	}

}
