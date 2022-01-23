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
		clearAndThenfillFormInput(INPUT_ISBN, isbn);
		clearAndThenfillFormInput(INPUT_TITLE, title);
		clearAndThenfillFormInput(INPUT_AUTHORS, authors);
		return pressSubmitButton();
	}

	public String getIsbnInputValue() {
		return getInputValue(INPUT_ISBN);
	}

	public String getIsbnValidationErrorMessage() {
		return getMessage(INPUT_ISBN + VALIDATION_ERROR_SUFFIX);
	}

}
