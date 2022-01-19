package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithCreateUpdateForm;

public class BookNewPage extends APageWithCreateUpdateForm {

	private static final String ISBN = "isbn";
	private static final String EXPECTED_TITLE = "Book new view";

	public BookNewPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public MyPage fillAddFormAndPressSubmitButton(String isbn, String title, String authors) {
		clearAndThenfillFormInput(this, ISBN, isbn);
		clearAndThenfillFormInput(this, TITLE, title);
		clearAndThenfillFormInput(this, AUTHORS, authors);
		return pressSubmitButton(this);
	}

	public String getIsbnInputValue() {
		return getInputValue(this, ISBN);
	}

	public String getIsbnValidationErrorMessage() {
		return getMessage(this, ISBN + VALIDATION_ERROR);
	}

}
