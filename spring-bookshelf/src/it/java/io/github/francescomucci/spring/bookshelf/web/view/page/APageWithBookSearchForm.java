package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

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

	public MyPage fillSearchFormAndPressSubmitButton(String inputValue) {
		searchForm.fillSearchForm(inputValue);
		return searchForm.pressSubmitButton();
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

}
