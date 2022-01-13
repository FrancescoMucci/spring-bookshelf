package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.IPageWithForm;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookNewPage extends MyPage implements IPageWithForm{

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

}
