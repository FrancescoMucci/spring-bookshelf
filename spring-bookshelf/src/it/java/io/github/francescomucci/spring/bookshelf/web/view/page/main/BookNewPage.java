package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithCreateUpdateForm;

public class BookNewPage extends APageWithCreateUpdateForm {

	public BookNewPage(WebDriver webDriver) {
		super(webDriver, BOOK_NEW_VIEW);
	}

	public MyPage fillAddFormAndPressSubmitButton(String isbn, String title, String authors) {
		clearAndThenfillFormInput(this, INPUT_ISBN, isbn);
		clearAndThenfillFormInput(this, INPUT_TITLE, title);
		clearAndThenfillFormInput(this, INPUT_AUTHORS, authors);
		return pressSubmitButton(this);
	}

	public String getIsbnInputValue() {
		return getInputValue(this, INPUT_ISBN);
	}

	public String getIsbnValidationErrorMessage() {
		return getMessage(this, INPUT_ISBN + VALIDATION_ERROR_SUFFIX);
	}

}
