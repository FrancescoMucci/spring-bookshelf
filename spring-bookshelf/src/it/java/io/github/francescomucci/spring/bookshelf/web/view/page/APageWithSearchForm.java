package io.github.francescomucci.spring.bookshelf.web.view.page;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

public abstract class APageWithSearchForm extends APageWithBookTable implements IPageWithForm {

	private String inputName;

	public APageWithSearchForm(WebDriver webDriver, String inputName) {
		super(webDriver);
		setInputName(inputName);
	}

	public APageWithSearchForm(WebDriver webDriver, String expectedTitle, String inputName) {
		super(webDriver, expectedTitle);
		setInputName(inputName);
	}

	public MyPage fillSearchFormAndPressSubmitButton(String inputValue) {
		clearAndThenfillFormInput(this, inputName, inputValue);
		return pressSubmitButton(this);
	}

	public String getInputValue() {
		return getInputValue(this, inputName);
	}

	public String getValidationErrorMessage() {
		return getMessage(this, inputName + VALIDATION_ERROR_SUFFIX);
	}

	private void setInputName(String inputName) {
		this.inputName = inputName;
	}

}
