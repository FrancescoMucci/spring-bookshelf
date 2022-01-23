package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookSearchForm;

public class BookSearchByTitlePage extends APageWithBookSearchForm {

	public BookSearchByTitlePage(WebDriver webDriver) {
		super(webDriver, BOOK_SEARCH_BY_TITLE_VIEW, INPUT_TITLE);
	}

}
