package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookEditPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookListPage;

public abstract class APageWithBookTable extends MyPage {

	@FindBy(id = "book-table")
	private WebElement bookTable;

	public APageWithBookTable(WebDriver webDriver) {
		super(webDriver);
	}

	public APageWithBookTable(WebDriver webDriver, String expectedTitle) {
		super(webDriver, expectedTitle);
	}

	public String getBookTable() {
		return bookTable.getText();
	}

	public BookEditPage clickEditLink(Long isbn) {
		clickBookEditLink(isbn);
		return new BookEditPage(webDriver);
	}
	
	public MyErrorPage clickEditLinkExpectingError(Long isbn) {
		clickBookEditLink(isbn);
		return new MyErrorPage(webDriver);
	}

	public BookListPage clickDeleteAndThenYes(Long isbn) {
		clickShowDeleteDialogAndThenYesDeleteButton(isbn);
		return new BookListPage(webDriver);
	}

	public MyErrorPage clickDeleteAndThenYesExpectingError(Long isbn) {
		clickShowDeleteDialogAndThenYesDeleteButton(isbn);
		return new MyErrorPage(webDriver);
	}

	private void clickBookEditLink(Long isbn) {
		webDriver.findElement(By.cssSelector("a[href='/book/edit/" + isbn + "']")).click();
	}
	
	private void clickShowDeleteDialogAndThenYesDeleteButton(Long isbn) {
		webDriver.findElement(By.id("getDeleteBookDialogButton-" + isbn)).click();
		WebDriverWait wait = new WebDriverWait(webDriver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("deleteBookDialog-" + isbn + "-yesButton")));
		webDriver.findElement(By.id("deleteBookDialog-" + isbn + "-yesButton")).click();
	}

}
