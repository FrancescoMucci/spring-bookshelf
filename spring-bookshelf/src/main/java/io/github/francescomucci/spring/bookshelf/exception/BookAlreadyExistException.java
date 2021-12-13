package io.github.francescomucci.spring.bookshelf.exception;

public class BookAlreadyExistException extends IllegalArgumentException {

	static final long serialVersionUID = 1L;

	public static final String BOOK_ALREADY_EXIST_MSG = ": a book with this ISBN-13 already exist";

	public BookAlreadyExistException(long isbn) {
		super(isbn + BOOK_ALREADY_EXIST_MSG);
	}

}
