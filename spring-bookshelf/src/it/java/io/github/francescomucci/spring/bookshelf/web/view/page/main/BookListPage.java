package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookTable;

public class BookListPage extends APageWithBookTable {

	public BookListPage(WebDriver webDriver) {
		super(webDriver, "Book list view");
	}

}
