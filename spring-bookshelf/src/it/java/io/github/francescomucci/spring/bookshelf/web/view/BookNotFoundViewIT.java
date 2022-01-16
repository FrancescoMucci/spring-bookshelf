package io.github.francescomucci.spring.bookshelf.web.view;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
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

import io.github.francescomucci.spring.bookshelf.repository.BookRepository;
import io.github.francescomucci.spring.bookshelf.web.view.page.helper.SilentHtmlUnitDriver;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookNotFoundViewIT {

	@Autowired
	private BookRepository bookRepository;

	private String bookNotFoundPageUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		bookRepository.deleteAll();
		bookNotFoundPageUrl = "http://localhost:" + portNumber + "/book/edit/" + UNUSED_ISBN13;
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookNotFoundView content tests ---------- */

	@Test
	public void testBookNotFoundView_content() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		
		assertThat(errorPage.getPageTitle())
			.isEqualTo("Book not found error view");
		assertThat(errorPage.getErrorCode())
			.isEqualTo("404");
		assertThat(errorPage.getErrorMessage())
			.isEqualTo(UNUSED_ISBN13 + ": no book found with this ISBN-13");
	}

	/* ---------- BookNotFoundView navigation bar tests ---------- */

	@Test
	public void testBookNotFoundView_homePageLink_canOpenBookHomePage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookNotFoundView_showBookListLink_canOpenBookListPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookNotFoundView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookNotFoundView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookNotFoundView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookNotFoundView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(bookNotFoundPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		BookHomePage bookHomePage = (BookHomePage) errorPage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
