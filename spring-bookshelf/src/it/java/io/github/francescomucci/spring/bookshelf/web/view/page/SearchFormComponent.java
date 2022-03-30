package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

public class SearchFormComponent extends FormComponent {

	String inputName;

	public SearchFormComponent(WebDriver webDriver, String inputName) {
		super(webDriver);
		this.inputName = inputName;
	}

	public void fillSearchForm(String inputValue) {
		clearAndThenfillFormInput(inputName, inputValue);
	}

	public String getInputValue() {
		return getInputValue(inputName);
	}

	public String getValidationErrorMessage() {
		return getMessage(inputName + "-validation-error");
	}
}
