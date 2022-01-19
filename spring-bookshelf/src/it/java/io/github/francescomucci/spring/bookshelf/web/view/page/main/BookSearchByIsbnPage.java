package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookTable;
import io.github.francescomucci.spring.bookshelf.web.view.page.IPageWithForm;

public class BookSearchByIsbnPage extends APageWithBookTable implements IPageWithForm {

	public BookSearchByIsbnPage(WebDriver webDriver) {
		super(webDriver, BOOK_SEARCH_BY_ISBN_VIEW);
	}

	public MyPage fillSearchFormAndPressSubmitButton(String inputValue) {
		clearAndThenfillFormInput(this, INPUT_ISBN, inputValue);
		return pressSubmitButton(this);
	}

	public String getInputValue() {
		return getInputValue(this, INPUT_ISBN);
	}

	public String getValidationErrorMessage() {
		return getMessage(this, INPUT_ISBN + VALIDATION_ERROR_SUFFIX);
	}

}
