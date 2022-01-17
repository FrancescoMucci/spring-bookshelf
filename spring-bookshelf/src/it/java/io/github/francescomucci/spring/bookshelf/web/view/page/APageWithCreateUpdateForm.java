package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

public abstract class APageWithCreateUpdateForm extends MyPage implements IPageWithForm {

	public APageWithCreateUpdateForm(WebDriver webDriver) {
		super(webDriver);
	}

	public APageWithCreateUpdateForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
	}

	public String getTitleValidationErrorMessage() {
		return getMessage(this,"title-validation-error");
	}

	public String getAuthorsValidationErrorMessage() {
		return getMessage(this,"authors-validation-error");
	}

}
