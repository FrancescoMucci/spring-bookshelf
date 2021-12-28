package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
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
