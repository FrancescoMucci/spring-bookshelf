package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookEditPage extends MyPage {

	private static final String EXPECTED_TITLE = "Book edit view";

	public BookEditPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

}
