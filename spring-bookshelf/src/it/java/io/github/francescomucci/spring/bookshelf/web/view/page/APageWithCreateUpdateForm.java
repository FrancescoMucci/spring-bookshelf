package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

public abstract class APageWithCreateUpdateForm extends MyPage implements IPageWithForm {

	protected static final String TITLE = "title";
	protected static final String AUTHORS = "authors";
	protected static final String VALIDATION_ERROR = "-validation-error";

	public APageWithCreateUpdateForm(WebDriver webDriver) {
		super(webDriver);
	}

	public APageWithCreateUpdateForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
	}

	public String getTitleInputValue() {
		return getInputValue(this, TITLE);
	}

	public String getTitleValidationErrorMessage() {
		return getMessage(this, TITLE + VALIDATION_ERROR);
	}

	public String getAuthorsInputValue() {
		return getInputValue(this, AUTHORS);
	}

	public String getAuthorsValidationErrorMessage() {
		return getMessage(this, AUTHORS + VALIDATION_ERROR);
	}

}
