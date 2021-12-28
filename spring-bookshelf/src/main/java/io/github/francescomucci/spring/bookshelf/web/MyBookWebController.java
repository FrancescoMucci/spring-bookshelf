package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
import io.github.francescomucci.spring.bookshelf.exception.InvalidIsbnException;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.service.BookService;

@Controller("BookWebController")
public class MyBookWebController implements BookWebController {

	@Autowired
	private BookService service;

	@Override
	public String getBookHomeView() {
		return VIEW_BOOK_HOME;
	}

	@Override
	public String getBookListView(Model model) {
		List<Book> bookList = service.getAllBooks();
		if (bookList.isEmpty()) {
			model.addAttribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB);
		} else {
			model.addAttribute(MODEL_BOOKS, bookList);
		}
		return VIEW_BOOK_LIST;
	}

	@Override
	public String postDeleteBook(IsbnData isbn, BindingResult result) {
		if (result.hasErrors())
			throw new InvalidIsbnException(isbn.getIsbn());
		service.delateBookByIsbn(isbn.toLong());
		return REDIRECT + URI_BOOK_LIST;
	}

	@Override
	public String getBookEditView(IsbnData isbn, BindingResult result, BookData editFormData) {
		return null;
	}

	@Override
	public String postSaveBook(BookData editFormData, BindingResult result) {
		return null;
	}

	@Override
	public String getBookNewView(BookData addFormData) {
		return null;
	}

	@Override
	public String postAddBook(BookData addFormData, BindingResult result) {
		return null;
	}

	@Override
	public String getBookSearchByIsbnView(BookData searchFormData) {
		return null;
	}

	@Override
	public String getBookByIsbn(BookData searchFormData, BindingResult result, Model model) {
		return null;
	}

	@Override
	public String getBookSearchByTitleView(BookData searchFormData) {
		return null;
	}

	@Override
	public String getBookByTitle(BookData searchFormData, BindingResult result, Model model) {
		return null;
	}

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

	private void addErrorModelAttributes(Exception exception, Model model, HttpStatus httpStatus) {
		model.addAttribute(MODEL_ERROR_CODE, httpStatus.value());
		model.addAttribute(MODEL_ERROR_REASON, httpStatus.getReasonPhrase());
		model.addAttribute(MODEL_ERROR_MESSAGE, exception.getMessage());
	}

}
