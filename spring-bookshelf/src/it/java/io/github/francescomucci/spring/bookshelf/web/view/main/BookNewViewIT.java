package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.view.page.helper.AuthenticationHelperMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookNewPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.helper.SilentHtmlUnitDriver;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookNewViewIT {

	@Autowired
	private BookRepository bookRepository;

	private String bookNewUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		bookRepository.deleteAll();
		bookNewUrl = "http://localhost:" + portNumber + URI_BOOK_NEW;
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookNewView functionality tests ---------- */

	@Test
	public void testBookNewView_addNewBookForm_withValidDataButAlreadyUsed_canOpenBookAlreadyExistPage() {
		Book alreadyExistingbook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.fillAddFormAndPressSubmitButton("" + alreadyExistingbook.getIsbn(), NEW_TITLE, AUTHORS_STRING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book already exist error view");
		assertThat(bookRepository.findById(alreadyExistingbook.getIsbn()))
			.contains(alreadyExistingbook);
	}

	@Test
	public void testBookNewView_addNewBookForm_withValidDataAndUnused_canAddNewBookIntoRepository() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.fillAddFormAndPressSubmitButton(VALID_ISBN13_WITH_SPACES, TITLE, AUTHORS_STRING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
		assertThat(bookRepository.findById(VALID_ISBN13))
			.contains(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testBookNewView_addNewBookForm_withInvalidData_cannotAddNewBookIntoRepository() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.fillAddFormAndPressSubmitButton(INVALID_ISBN13_WITH_SPACES, INVALID_TITLE, INVALID_AUTHORS_STRING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
		assertThat(bookRepository.findById(VALID_ISBN13))
			.isEmpty();
	}

	/* ---------- BookNewView navigation bar tests ---------- */

	@Test
	public void testBookNewView_homePageLink_canOpenBookHomePage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookNewView_showBookListLink_canOpenBookListPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookNewView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookNewView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookNewView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		MyPage returnedPage = bookNewPage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookNewView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(bookNewUrl);
		BookNewPage bookNewPage = new BookNewPage(webDriver);
		BookHomePage bookHomePage = (BookHomePage) bookNewPage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
