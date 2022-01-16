package io.github.francescomucci.spring.bookshelf.web.view;

import static io.github.francescomucci.spring.bookshelf.web.view.page.helper.AuthenticationHelperMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.helper.SilentHtmlUnitDriver;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ErrorViewIT {

	private String unknownErrorPageUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		unknownErrorPageUrl = "http://localhost:" + portNumber + "/book/nonExisting";
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- ErrorView content tests ---------- */

	@Test
	public void testErrorView_content() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		
		assertThat(errorPage.getPageTitle())
			.isEqualTo("Unknown error view");
		assertThat(errorPage.getErrorCode())
			.isEqualTo("404");
		assertThat(errorPage.getErrorReason())
			.isEqualTo("Not Found");
	}

	/* ---------- ErrorView navigation bar tests ---------- */

	@Test
	public void testErrorView_homePageLink_canOpenBookHomePage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testErrorView_showBookListLink_canOpenBookListPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testErrorView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testErrorView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testErrorView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		MyPage returnedPage = errorPage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testErrorView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(unknownErrorPageUrl);
		MyErrorPage errorPage = new MyErrorPage(webDriver);
		BookHomePage bookHomePage = (BookHomePage) errorPage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
