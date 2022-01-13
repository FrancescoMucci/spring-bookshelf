package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookTable;
import io.github.francescomucci.spring.bookshelf.web.view.page.IPageWithForm;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookSearchByTitlePage extends APageWithBookTable implements IPageWithForm {

	private static final String EXPECTED_TITLE = "Book search by title view";
	private static final String TITLE = "title";

	public BookSearchByTitlePage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public MyPage fillSearchFormAndPressSubmitButton(String inputValue) {
		clearAndThenfillFormInput(this, TITLE, inputValue);
		return pressSubmitButton(this);
	}

	public String getValidationErrorMessage() {
		return getMessage(this, TITLE + "-validation-error");
	}

}
