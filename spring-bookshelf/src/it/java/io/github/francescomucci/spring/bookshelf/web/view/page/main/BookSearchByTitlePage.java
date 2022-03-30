package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookSearchForm;

public class BookSearchByTitlePage extends APageWithBookSearchForm {

	public BookSearchByTitlePage(WebDriver webDriver) {
		super(webDriver, "Book search by title view", INPUT_TITLE);
	}

	public BookSearchByTitlePage fillSearchFormAndSubmit(String inputValue) {
		fillSearchFormAndPressSubmitButton(inputValue);
		return this;
	}

}
