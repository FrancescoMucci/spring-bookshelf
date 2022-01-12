package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookSearchByIsbnPage extends MyPage {

	private static final String EXPECTED_TITLE = "Book search by ISBN view";

	public BookSearchByIsbnPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

}
