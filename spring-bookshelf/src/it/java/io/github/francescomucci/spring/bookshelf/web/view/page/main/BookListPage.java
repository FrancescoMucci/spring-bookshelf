package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookListPage extends MyPage {

	private static final String EXPECTED_TITLE = "Book list view";

	public BookListPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

}
