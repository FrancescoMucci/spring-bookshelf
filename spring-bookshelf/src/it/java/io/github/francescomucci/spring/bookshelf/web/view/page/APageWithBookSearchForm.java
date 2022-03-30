package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;

public abstract class APageWithBookSearchForm extends APageWithBookTable {

	private SearchFormComponent searchForm;

	public APageWithBookSearchForm(WebDriver webDriver, String inputName) {
		super(webDriver);
		initSearchForm(inputName);
	}

	public APageWithBookSearchForm(WebDriver webDriver, String expectedTitle, String inputName) {
		super(webDriver, expectedTitle);
		initSearchForm(inputName);
	}

	public MyErrorPage fillSearchFormAndSubmitExpectingError(String inputValue) {
		fillSearchFormAndPressSubmitButton(inputValue);
		return new MyErrorPage(webDriver);
	}

	public String getInputValue() {
		return searchForm.getInputValue();
	}

	public String getValidationErrorMessage() {
		return searchForm.getValidationErrorMessage();
	}

	public void initSearchForm(String inputName) {
		this.searchForm = new SearchFormComponent(webDriver, inputName);
	}

	protected void fillSearchFormAndPressSubmitButton(String inputValue) {
		searchForm.fillSearchForm(inputValue);
		searchForm.pressSubmitButton();
	}

}
