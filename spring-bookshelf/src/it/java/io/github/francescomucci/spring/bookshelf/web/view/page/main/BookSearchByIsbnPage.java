package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithSearchForm;

public class BookSearchByIsbnPage extends APageWithSearchForm{

	public BookSearchByIsbnPage(WebDriver webDriver) {
		super(webDriver, BOOK_SEARCH_BY_ISBN_VIEW, INPUT_ISBN);
	}

}
