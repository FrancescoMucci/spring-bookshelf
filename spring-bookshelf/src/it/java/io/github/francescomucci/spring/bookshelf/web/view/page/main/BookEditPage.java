package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookCUForm;

public class BookEditPage extends APageWithBookCUForm {

	public BookEditPage(WebDriver webDriver) {
		super(webDriver, BOOK_EDIT_VIEW);
	}

	public BookListPage fillEditFormAndSubmit(String title, String authors) {
		fillEditFormAndPressSubmitButton(title, authors);
		return new BookListPage(webDriver);
	}

	public MyErrorPage fillEditFormAndSubmitExpectingError(String title, String authors) {
		fillEditFormAndPressSubmitButton(title, authors);
		return new MyErrorPage(webDriver);
	}

	public BookEditPage fillEditFormAndSubmitExpectingValidationError(String title, String authors) {
		fillEditFormAndPressSubmitButton(title, authors);
		return this;
	}

	private void fillEditFormAndPressSubmitButton(String title, String authors) {
		form.clearAndThenfillFormInput(INPUT_TITLE, title);
		form.clearAndThenfillFormInput(INPUT_AUTHORS, authors);
		form.pressSubmitButton();
	}

}
