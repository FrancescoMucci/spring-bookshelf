package io.github.francescomucci.spring.bookshelf.web.view.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookListPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookNewPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByIsbnPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByTitlePage;

public class MyPage {

	protected WebDriver webDriver;

	@FindBy(tagName = "header")
	private WebElement header;

	@FindBy(linkText = "Mucci's bookshelf")
	private WebElement homeLink;
	
	@FindBy(linkText = "Show book list")
	private WebElement bookListLink;

	@FindBy(linkText = "Search book by isbn")
	private WebElement searchBookByIsbnLink;

	@FindBy(linkText = "Search book by title")
	private WebElement searchBooksByTitle;

	@FindBy(linkText = "Add new book")
	private WebElement addNewBookLink;

	@FindBy(name = "logout-button")
	private WebElement logoutButton;

	public MyPage(WebDriver webDriver) {
		this.webDriver = webDriver;
		PageFactory.initElements(webDriver, this);
	}

	public MyPage(WebDriver webDriver, String expectedTitle) {
		this(webDriver);
		verifyTitle(expectedTitle);
	}

	protected void verifyTitle(String expectedTitle) {
		assertThat(this.getPageTitle())
			.isEqualTo(expectedTitle);
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
		searchBooksByTitle.click();
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
