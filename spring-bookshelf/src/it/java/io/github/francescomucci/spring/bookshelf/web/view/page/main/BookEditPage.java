package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookCUForm;

public class BookEditPage extends APageWithBookCUForm {

	public BookEditPage(WebDriver webDriver) {
		super(webDriver, BOOK_EDIT_VIEW);
	}

	public MyPage fillEditFormAndPressSubmitButton(String title, String authors) {
		clearAndThenfillFormInput(INPUT_TITLE, title);
		clearAndThenfillFormInput(INPUT_AUTHORS, authors);
		return pressSubmitButton();
	}

}
