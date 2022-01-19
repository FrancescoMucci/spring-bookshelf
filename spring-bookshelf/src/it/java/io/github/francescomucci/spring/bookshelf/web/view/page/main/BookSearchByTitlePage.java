package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.view.page.BookFormConstants.*;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithBookTable;
import io.github.francescomucci.spring.bookshelf.web.view.page.IPageWithForm;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookSearchByTitlePage extends APageWithBookTable implements IPageWithForm {

	public BookSearchByTitlePage(WebDriver webDriver) {
		super(webDriver, BOOK_SEARCH_BY_TITLE_VIEW);
	}

	public MyPage fillSearchFormAndPressSubmitButton(String inputValue) {
		clearAndThenfillFormInput(this, INPUT_TITLE, inputValue);
		return pressSubmitButton(this);
	}

	public String getInputValue() {
		return getInputValue(this, INPUT_TITLE);
	}

	public String getValidationErrorMessage() {
		return getMessage(this, INPUT_TITLE + VALIDATION_ERROR_SUFFIX);
	}

}
