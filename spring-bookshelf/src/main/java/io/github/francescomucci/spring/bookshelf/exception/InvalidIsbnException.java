package io.github.francescomucci.spring.bookshelf.exception;

public class InvalidIsbnException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public static final String INVALID_ISBN_MSG = ": invalid ISBN-13";

	public InvalidIsbnException(String isbn) {
		super(isbn + INVALID_ISBN_MSG);
	}

}
