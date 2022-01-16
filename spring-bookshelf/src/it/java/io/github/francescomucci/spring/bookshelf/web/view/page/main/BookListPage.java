package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookTable;

public class BookListPage extends APageWithBookTable {

	private static final String EXPECTED_TITLE = "Book list view";

	public BookListPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

}
