package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;

public abstract class APageWithForm extends MyPage {

	protected FormComponent form;

	public APageWithForm(WebDriver webDriver) {
		super(webDriver);
		initForm();
	}

	public APageWithForm(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
		initForm();
	}

	private void initForm() {
		form = new FormComponent(webDriver);
	}

}
