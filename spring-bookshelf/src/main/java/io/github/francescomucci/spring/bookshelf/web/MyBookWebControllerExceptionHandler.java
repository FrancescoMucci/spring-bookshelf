package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.github.francescomucci.spring.bookshelf.exception.BookAlreadyExistException;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.exception.InvalidIsbnException;

@ControllerAdvice
public class MyBookWebControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidIsbnException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private String handleInvalidIsbnException(InvalidIsbnException exception, Model model) {
		addErrorModelAttributes(exception, model, HttpStatus.BAD_REQUEST);
		return ERROR_INVALID_ISBN;
	}

	@ExceptionHandler(BookNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	private String handleBookNotFoundException(BookNotFoundException exception, Model model) {
		addErrorModelAttributes(exception, model, HttpStatus.NOT_FOUND);
		return ERROR_BOOK_NOT_FOUND;
	}

	@ExceptionHandler(BookAlreadyExistException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	private String handleBookAlreadyExistException(BookAlreadyExistException exception, Model model) {
		addErrorModelAttributes(exception, model, HttpStatus.CONFLICT);
		return ERROR_BOOK_ALREADY_EXIST;
	}

	private void addErrorModelAttributes(Exception exception, Model model, HttpStatus httpStatus) {
		model.addAttribute(MODEL_ERROR_CODE, httpStatus.value());
		model.addAttribute(MODEL_ERROR_REASON, httpStatus.getReasonPhrase());
		model.addAttribute(MODEL_ERROR_MESSAGE, exception.getMessage());
	}

}
