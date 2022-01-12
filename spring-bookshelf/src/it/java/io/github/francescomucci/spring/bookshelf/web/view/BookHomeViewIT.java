package io.github.francescomucci.spring.bookshelf.web.view;

import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
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
import io.github.francescomucci.spring.bookshelf.web.view.page.helper.SilentHtmlUnitDriver;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookHomeViewIT {

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	private BookHomePage bookHomePage;

	@Before
	public void setup() {
		webDriver = new SilentHtmlUnitDriver();
		webDriver.get("http://localhost:" + portNumber);
		bookHomePage = new BookHomePage(webDriver);
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookHomeView functionality tests ---------- */

	@Test
	public void testBookHomeView_loginForm_whenInvalidCredentials_cannotLoginSuccesfully() {
		bookHomePage.fillLoginFormAndPressSubmitButton(INVALID_USER_NAME, INVALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome to my book library");
		assertThat(bookHomePage.getAuthenticationErrorMessage())
			.isEqualTo("Invalid user name or password");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

	@Test
	public void testBookHomeView_loginForm_whenValidCredentials_canLoginSuccesfully() {
		bookHomePage.fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome back");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

	@Test
	public void testBookHomeView_loginForm_whenInvalidCredentialsAndRememberMe_cannotSaveRememberMeCookie() {
		bookHomePage.checkRemeberMe();
		bookHomePage.fillLoginFormAndPressSubmitButton(INVALID_USER_NAME, INVALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome to my book library");
		assertThat(bookHomePage.getAuthenticationErrorMessage())
			.isEqualTo("Invalid user name or password");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

	@Test
	public void testBookHomeView_loginForm_whenValidCredentialsAndRememberMe_canSaveRememberMeCookie() {
		bookHomePage.checkRemeberMe();
		bookHomePage.fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome back");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNotNull();
	}

	/* ---------- BookHomeView navigation bar tests ---------- */

	@Test
	public void testBookHomeView_homePageLink_canOpenBookHomePage() {
		MyPage returnedPage = bookHomePage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookHomeView_showBookListLink_canOpenBookListPage() {
		MyPage returnedPage = bookHomePage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookHomeView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		MyPage returnedPage = bookHomePage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookHomeView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		MyPage returnedPage = bookHomePage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookHomeView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		MyPage returnedPage = bookHomePage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookEditView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		bookHomePage = (BookHomePage) bookHomePage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
