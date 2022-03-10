package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

public abstract class APageWithForm extends MyPage {

	private FormComponent form;

	public APageWithForm(WebDriver webDriver) {
		super(webDriver);
		initForm();
	}

	public APageWithForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
		initForm();
	}

	public void clearAndThenfillFormInput(String inputName, String inputValue) {
		form.clearAndThenfillFormInput(inputName, inputValue);
	}

	public MyPage pressSubmitButton() {
		return form.pressSubmitButton();
	}

	public String getInputValue(String inputName) {
		return form.getInputValue(inputName);
	}

	public String getMessage(String messageId) {
		return form.getMessage(messageId);
	}

	private void initForm() {
		form = new FormComponent(webDriver);
	}

}
