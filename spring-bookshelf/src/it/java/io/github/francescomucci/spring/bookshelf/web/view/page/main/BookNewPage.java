package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookCUForm;

public class BookNewPage extends APageWithBookCUForm {

	public BookNewPage(WebDriver webDriver) {
		super(webDriver, BOOK_NEW_VIEW);
	}

	public BookListPage fillAddFormAndSubmit(String isbn, String title, String authors) {
		fillAddFormAndPressSubmitButton(isbn, title, authors);
		return new BookListPage(webDriver);
	}

	public MyErrorPage fillAddFormAndSubmitExpectingError(String isbn, String title, String authors) {
		fillAddFormAndPressSubmitButton(isbn, title, authors);
		return new MyErrorPage(webDriver);
	}

	public BookNewPage fillAddFormAndSubmitExpectingValidationError(String isbn, String title, String authors) {
		fillAddFormAndPressSubmitButton(isbn, title, authors);
		return this;
	}

	public String getIsbnInputValue() {
		return form.getInputValue(INPUT_ISBN);
	}

	public String getIsbnValidationErrorMessage() {
		return form.getMessage(INPUT_ISBN + VALIDATION_ERROR_SUFFIX);
	}

	private void fillAddFormAndPressSubmitButton(String isbn, String title, String authors) {
		form.clearAndThenfillFormInput(INPUT_ISBN, isbn);
		form.clearAndThenfillFormInput(INPUT_TITLE, title);
		form.clearAndThenfillFormInput(INPUT_AUTHORS, authors);
		form.pressSubmitButton();
	}

}
