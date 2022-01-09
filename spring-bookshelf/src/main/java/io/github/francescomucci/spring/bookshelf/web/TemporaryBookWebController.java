package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;


import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.exception.InvalidIsbnException;

/* Temporary fake implementation of BookWebController only to manual test web-pages esthetics*/

@Controller("TemporaryBookWebController")
public class TemporaryBookWebController implements BookWebController {


	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	private static final long ISBN_1 = 9788804395942L;
	private static final String TITLE_1 = "Foundation";
	private static final List<String> AUTHORS_1 = asList("Isaac Asimov");
	private static final long NEW_ISBN_1 = 9780553293357L;
	
	private static final long ISBN_2 = 9781401238964L;
	private static final String TITLE_2 = "Watchmen";
	private static final List<String> AUTHORS_2 = asList("Alan Moore", "Dave Gibbons");

	@Override
	public String getBookHomeView() {
		return VIEW_BOOK_HOME;
	}

	@Override
	public String getBookListView(Model model) {
		List<Book> bookList = asList(
			new Book(ISBN_1, TITLE_1, AUTHORS_1), 
			new Book(ISBN_2, TITLE_2, AUTHORS_2));
		model.addAttribute(MODEL_BOOKS, bookList);
		return VIEW_BOOK_LIST;
	}

	/* Temporary web end-point only to manual test bookList view esthetics when DB is empty */
	@GetMapping(URI_BOOK_LIST + "/test/empty")
	public String getBookListViewTestEmpty(Model model) {
		model.addAttribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB);
		return VIEW_BOOK_LIST;
	}

	@Override
	public String postDeleteBook(IsbnData isbn, BindingResult result) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookEditView(IsbnData isbn, BindingResult result, BookData editFormData) {
		editFormData.setIsbn(isbn.getIsbn());
		editFormData.setTitle(TITLE_1);
		editFormData.setAuthors("Isaac Asimov");
		return VIEW_BOOK_EDIT;
	}

	@Override
	public String postSaveBook(BookData editFormData, BindingResult result) {
		return VIEW_BOOK_EDIT;
	}

	@Override
	public String getBookNewView(BookData addFormData) {
		return VIEW_BOOK_NEW;
	}

	@Override
	public String postAddBook(BookData addFormData, BindingResult result) {
		return VIEW_BOOK_NEW;
	}

	@Override
	public String getBookSearchByIsbnView(BookData searchFormData) {
		return VIEW_BOOK_SEARCH_BY_ISBN;
	}

	@Override
	public String getBookByIsbn(BookData searchFormData, BindingResult result, Model model) {
		return VIEW_BOOK_SEARCH_BY_ISBN;
	}

	/* Temporary web end-point only to manual test searchByIsbn view esthetics when a book is found */
	@GetMapping(URI_BOOK_GET_BY_ISBN + "/test/found")
	public String getBookByIsbnTestFound(BookData searchFormData, BindingResult result, Model model) {
		model.addAttribute(MODEL_BOOKS, new Book(ISBN_1, TITLE_1,  AUTHORS_1));
		return VIEW_BOOK_SEARCH_BY_ISBN;
	}

	@Override
	public String getBookSearchByTitleView(BookData searchFormData) {
		return VIEW_BOOK_SEARCH_BY_TITLE;
	}

	@Override
	public String getBookByTitle(BookData searchFormData, BindingResult result, Model model) {
		return VIEW_BOOK_SEARCH_BY_TITLE;
	}

	/* Temporary web end-point only to manual test searchByTitle view esthetics when books are found */
	@GetMapping(URI_BOOK_GET_BY_TITLE + "/test/found")
	public String getBookByTitleTestFound(BookData searchFormData, BindingResult result, Model model) {
		List<Book> bookList = asList(
				new Book(ISBN_1, TITLE_1, AUTHORS_1), 
				new Book(NEW_ISBN_1, TITLE_1 + " vol.1", AUTHORS_1));
		model.addAttribute(MODEL_BOOKS, bookList);
		return VIEW_BOOK_SEARCH_BY_TITLE;
	}

	/* Temporary web end-point only to manual test error view esthetics */
	@GetMapping(URI_BOOK_HOME + "/test/error")
	public String getErrorView() throws Exception {
		throw new Exception("Error view esthetics test");
	}

	/* Temporary web end-point only to manual test bookNotFound view esthetics */
	@GetMapping(URI_BOOK_HOME + "/test/bookNotFound")
	public String getBookNotFoundView(Model model) {
		addErrorModelAttributes(model, HttpStatus.NOT_FOUND, ISBN_2 + BookNotFoundException.BOOK_NOT_FOUND_MSG);
		return ERROR_BOOK_NOT_FOUND;
	}

	/* Temporary web end-point only to manual test invalidIsbn view esthetics */
	@GetMapping(URI_BOOK_HOME + "/test/invalidIsbn")
	public String getInvalidIsbnView(Model model) {
		addErrorModelAttributes(model, HttpStatus.BAD_REQUEST, 1234567890123L + InvalidIsbnException.INVALID_ISBN_MSG);
		return ERROR_INVALID_ISBN;
	}

	private void addErrorModelAttributes(Model model, HttpStatus httpStatus, String message) {
		model.addAttribute(MODEL_ERROR_CODE, httpStatus.value());
		model.addAttribute(MODEL_ERROR_REASON, httpStatus.getReasonPhrase());
		model.addAttribute(MODEL_ERROR_MESSAGE, message);
	}

}
