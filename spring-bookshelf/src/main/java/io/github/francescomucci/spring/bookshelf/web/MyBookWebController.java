package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
import io.github.francescomucci.spring.bookshelf.exception.InvalidIsbnException;
import io.github.francescomucci.spring.bookshelf.service.BookService;
import io.github.francescomucci.spring.bookshelf.web.dto.BookDataMapper;

@Controller("BookWebController")
public class MyBookWebController implements BookWebController {

	@Autowired
	private BookService service;

	@Autowired
	private BookDataMapper map;

	@Override
	public String getBookHomeView() {
		return VIEW_BOOK_HOME;
	}

	@Override
	public String getBookListView(Model model) {
		List<Book> bookList = service.getAllBooks();
		if (!bookList.isEmpty())
			model.addAttribute(MODEL_BOOKS, toBookDataList(bookList));
		return VIEW_BOOK_LIST;
	}

	@Override
	public String postDeleteBook(IsbnData isbn, BindingResult result) {
		if (result.hasErrors())
			throw new InvalidIsbnException(isbn.getIsbn());
		service.delateBookByIsbn(map.toLong(isbn));
		return REDIRECT + URI_BOOK_LIST;
	}

	@Override
	public String getBookEditView(BookData editFormData, BindingResult result) {
		if (result.hasErrors())
			throw new InvalidIsbnException(editFormData.getIsbn());
		BookData bookToEditData = map.toBookData(service.getBookByIsbn(map.toLong(editFormData)));
		editFormData.setTitle(bookToEditData.getTitle());
		editFormData.setAuthors(bookToEditData.getAuthors());
		return VIEW_BOOK_EDIT;
	}

	@Override
	public String postSaveBook(BookData editFormData, BindingResult result) {
		if (!result.hasErrors()) {
			service.replaceBook(map.toBook(editFormData));
			return REDIRECT + URI_BOOK_LIST;
		}
		if (result.hasFieldErrors("isbn"))
			throw new InvalidIsbnException(editFormData.getIsbn());
		return VIEW_BOOK_EDIT;
	}

	@Override
	public String getBookNewView(BookData addFormData) {
		return VIEW_BOOK_NEW;
	}

	@Override
	public String postAddBook(BookData addFormData, BindingResult result) {
		if (result.hasErrors()) 
			return VIEW_BOOK_NEW;
		service.addNewBook(map.toBook(addFormData));
		return REDIRECT + URI_BOOK_LIST;
	}

	@Override
	public String getBookSearchByIsbnView(BookData searchByIsbnFormData) {
		return VIEW_BOOK_SEARCH_BY_ISBN;
	}

	@Override
	public String getBookByIsbn(BookData searchByIsbnFormData, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			Book foundedBook = service.getBookByIsbn(map.toLong(searchByIsbnFormData));
			model.addAttribute(MODEL_BOOKS, map.toBookData(foundedBook));
		}
		return VIEW_BOOK_SEARCH_BY_ISBN;
	}

	@Override
	public String getBookSearchByTitleView(BookData searchByTitleFormData) {
		return VIEW_BOOK_SEARCH_BY_TITLE;
	}

	@Override
	public String getBookByTitle(BookData searchByTitleFormData, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			List<Book> foundedBookList = service.getBooksByTitle(searchByTitleFormData.getTitle());
			model.addAttribute(MODEL_BOOKS, toBookDataList(foundedBookList));
		}
		return VIEW_BOOK_SEARCH_BY_TITLE;
	}

	private List<BookData> toBookDataList(List<Book> bookList){
		return bookList.stream()
			.map(map::toBookData)
			.collect(Collectors.toList());
	}

}
