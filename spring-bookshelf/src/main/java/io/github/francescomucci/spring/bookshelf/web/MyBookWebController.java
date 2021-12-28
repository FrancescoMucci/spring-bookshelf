package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;

@Controller("BookWebController")
public class MyBookWebController implements BookWebController {

	@Override
	public String getBookHomeView() {
		return VIEW_BOOK_HOME;
	}

	@Override
	public String getBookListView(Model model) {
		return null;
	}

	@Override
	public String postDeleteBook(IsbnData isbn, BindingResult result) {
		return null;
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

}
