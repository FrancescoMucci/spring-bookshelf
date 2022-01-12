package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookNewPage extends MyPage {

	private static final String EXPECTED_TITLE = "Book new view";

	public BookNewPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

}
