package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;

/* Temporary fake implementation of BookWebController only to manual test web-pages esthetics*/

@Controller("TemporaryBookWebController")
public class TemporaryBookWebController implements BookWebController {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	@Override
	public String getBookHomeView() {
		return VIEW_BOOK_HOME;
	}

	@Override
	public String getBookListView(Model model) {
		return VIEW_BOOK_LIST;
	}

	@Override
	public String postDeleteBook(IsbnData isbn, BindingResult result) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookEditView(IsbnData isbn, BindingResult result, BookData editFormData) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String postSaveBook(BookData editFormData, BindingResult result) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookNewView(BookData addFormData) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String postAddBook(BookData addFormData, BindingResult result) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookSearchByIsbnView(BookData searchFormData) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookByIsbn(BookData searchFormData, BindingResult result, Model model) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookSearchByTitleView(BookData searchFormData) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public String getBookByTitle(BookData searchFormData, BindingResult result, Model model) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
