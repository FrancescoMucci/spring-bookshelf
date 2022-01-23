package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

public abstract class APageWithForm extends MyPage implements IPageWithForm {

	public APageWithForm(WebDriver webDriver) {
		super(webDriver);
	}

	public APageWithForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
	}

	public void clearAndThenfillFormInput(String inputName, String inputValue) {
		clearAndThenfillFormInput(this, inputName, inputValue);
	}

	public MyPage pressSubmitButton() {
		return pressSubmitButton(this);
	}

	public String getInputValue(String inputName) {
		return getInputValue(this, inputName);
	}

	public String getMessage(String messageId) {
		return getMessage(this, messageId);
	}

}
