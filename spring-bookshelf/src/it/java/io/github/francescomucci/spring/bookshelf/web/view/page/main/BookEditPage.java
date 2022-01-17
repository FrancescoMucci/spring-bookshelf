package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithCreateUpdateForm;

public class BookEditPage extends APageWithCreateUpdateForm {

	private static final String EXPECTED_TITLE = "Book edit view";

	public BookEditPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public MyPage fillEditFormAndPressSubmitButton(String title, String authors) {
		clearAndThenfillFormInput(this,"title", title);
		clearAndThenfillFormInput(this,"authors", authors);
		return pressSubmitButton(this);
	}

}
