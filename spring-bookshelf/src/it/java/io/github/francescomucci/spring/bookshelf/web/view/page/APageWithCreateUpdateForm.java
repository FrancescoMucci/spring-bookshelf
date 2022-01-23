package io.github.francescomucci.spring.bookshelf.web.view.page;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

public abstract class APageWithCreateUpdateForm extends APageWithForm {

	public APageWithCreateUpdateForm(WebDriver webDriver) {
		super(webDriver);
	}

	public APageWithCreateUpdateForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
	}

	public String getTitleInputValue() {
		return getInputValue(INPUT_TITLE);
	}

	public String getTitleValidationErrorMessage() {
		return getMessage(INPUT_TITLE + VALIDATION_ERROR_SUFFIX);
	}

	public String getAuthorsInputValue() {
		return getInputValue(INPUT_AUTHORS);
	}

	public String getAuthorsValidationErrorMessage() {
		return getMessage(INPUT_AUTHORS + VALIDATION_ERROR_SUFFIX);
	}

}
