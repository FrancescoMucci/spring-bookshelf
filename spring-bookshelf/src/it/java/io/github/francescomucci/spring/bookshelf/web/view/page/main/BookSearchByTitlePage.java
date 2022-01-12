package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookSearchByTitlePage extends MyPage {

	private static final String EXPECTED_TITLE = "Book search by title view";

	public BookSearchByTitlePage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

}
