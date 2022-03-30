package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookListPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookNewPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByIsbnPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByTitlePage;

public class MyPage extends APageObject {

	@FindBy(tagName = "header")
	private WebElement header;

	@FindBy(linkText = "Mucci's bookshelf")
	private WebElement homeLink;
	
	@FindBy(linkText = "Show book list")
	private WebElement bookListLink;

	@FindBy(linkText = "Search book by isbn")
	private WebElement searchBookByIsbnLink;

	@FindBy(linkText = "Search book by title")
	private WebElement searchBooksByTitleLink;

	@FindBy(linkText = "Add new book")
	private WebElement addNewBookLink;

	@FindBy(name = "logout-button")
	private WebElement logoutButton;

	public MyPage(WebDriver webDriver) {
		super(webDriver);
	}

	public MyPage(WebDriver webDriver, String expectedTitle) {
		super(webDriver);
		String actualTitle = getPageTitle();
		if (!actualTitle.equals(expectedTitle))
			throw new IllegalStateException(
				"Expected page: " + expectedTitle + 
				", Actual page: " + actualTitle);
	}

	public String getPageTitle() {
		return webDriver.getTitle();
	}

	public String getHeaderContent() {
		return header.getText();
	}

	public BookHomePage clickNavbarHomeLink() {
		homeLink.click();
		return new BookHomePage(webDriver);
	}

	public BookListPage clickNavbarShowBookListLink() {
		bookListLink.click();
		return new BookListPage(webDriver);
	}

	public BookSearchByIsbnPage clickNavbarSearchBookByIsbnLink() {
		searchBookByIsbnLink.click();
		return new BookSearchByIsbnPage(webDriver);
	}

	public BookSearchByTitlePage clickNavbarSearchBooksByTitleLink() {
		searchBooksByTitleLink.click();
		return new BookSearchByTitlePage(webDriver);
	}

	public BookNewPage clickNavbarAddNewBookLink() {
		addNewBookLink.click();
		return new BookNewPage(webDriver);
	}

	public BookHomePage clickLogoutButton() {
		logoutButton.click();
		return new BookHomePage(webDriver);
	}

}
