package io.github.francescomucci.spring.bookshelf.web.view.page;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

public abstract class APageWithCreateUpdateForm extends MyPage implements IPageWithForm {

	public APageWithCreateUpdateForm(WebDriver webDriver) {
		super(webDriver);
	}

	public APageWithCreateUpdateForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
	}

	public String getTitleInputValue() {
		return getInputValue(this, INPUT_TITLE);
	}

	public String getTitleValidationErrorMessage() {
		return getMessage(this, INPUT_TITLE + VALIDATION_ERROR_SUFFIX);
	}

	public String getAuthorsInputValue() {
		return getInputValue(this, INPUT_AUTHORS);
	}

	public String getAuthorsValidationErrorMessage() {
		return getMessage(this, INPUT_AUTHORS + VALIDATION_ERROR_SUFFIX);
	}

}
