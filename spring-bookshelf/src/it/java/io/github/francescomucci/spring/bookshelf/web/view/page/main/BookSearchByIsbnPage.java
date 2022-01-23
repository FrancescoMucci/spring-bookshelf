package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookSearchForm;

public class BookSearchByIsbnPage extends APageWithBookSearchForm{

	public BookSearchByIsbnPage(WebDriver webDriver) {
		super(webDriver, BOOK_SEARCH_BY_ISBN_VIEW, INPUT_ISBN);
	}

}
