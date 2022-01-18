package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithCreateUpdateForm;

public class BookNewPage extends APageWithCreateUpdateForm {

	private static final String EXPECTED_TITLE = "Book new view";

	public BookNewPage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public MyPage fillAddFormAndPressSubmitButton(String isbn, String title, String authors) {
		clearAndThenfillFormInput(this,"isbn", isbn);
		clearAndThenfillFormInput(this,"title", title);
		clearAndThenfillFormInput(this,"authors", authors);
		return pressSubmitButton(this);
	}

	public String getIsbnValidationErrorMessage() {
		return getMessage(this,"isbn-validation-error");
	}

}
