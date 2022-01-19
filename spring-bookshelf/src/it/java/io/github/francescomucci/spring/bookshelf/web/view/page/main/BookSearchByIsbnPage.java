package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookTable;
import io.github.francescomucci.spring.bookshelf.web.view.page.IPageWithForm;

public class BookSearchByIsbnPage extends APageWithBookTable implements IPageWithForm {

	private static final String EXPECTED_TITLE = "Book search by ISBN view";
	private static final String ISBN = "isbn";

	public BookSearchByIsbnPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public MyPage fillSearchFormAndPressSubmitButton(String inputValue) {
		clearAndThenfillFormInput(this, ISBN, inputValue);
		return pressSubmitButton(this);
	}

	public String getInputValue() {
		return getInputValue(this, ISBN);
	}

	public String getValidationErrorMessage() {
		return getMessage(this, ISBN + "-validation-error");
	}

}
