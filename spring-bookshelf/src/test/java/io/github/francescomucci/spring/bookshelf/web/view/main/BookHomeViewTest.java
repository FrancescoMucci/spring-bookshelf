package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlFooter;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeader;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.web.BookWebController;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.view.BookViewTestingHelperMethods;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
public class BookHomeViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getCookieManager().clearCookies();
	}

	/* ---------- BookHomeView header message tests ---------- */

	@Test
	public void testBookHomeView_whenNotAuthenticated_shouldContainAWelcomeMessageForAnonymousUsersInTheHeader() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlHeader header = (HtmlHeader) bookHomeView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Welcome to my book library!" + "\n" +
				"Feel free to explore, but remember that you need administrator privileges to create, edit or delete books");
	}

	@Test
	@WithMockAdmin
	public void testBookHomeView_whenAuthenticated_shouldContainAWelcomeMessageForAdminInTheHeader() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlHeader header = (HtmlHeader) bookHomeView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Welcome back!" + "\n" +
				"Feel free to explore, create, edit or delete books");
	}

	/* ---------- BookHomeView login form tests ---------- */

	@Test
	public void testBookHomeView_whenNotAuthenticated_shouldContainTheLoginForm() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		assertThat(loginForm.getElementsByTagName("label").get(0).asText())
			.isEqualTo("User");
		assertThat(loginForm.getInputByName("username").getPlaceholder())
			.isEqualTo("e.g. Admin");
		
		assertThat(loginForm.getElementsByTagName("label").get(1).asText())
			.isEqualTo("Password");
		assertThat(loginForm.getInputByName("password").getPlaceholder())
			.isEqualTo("e.g. Password");
		
		assertThat(loginForm.getButtonByName("submit-button").asText())
			.isEqualTo("Login");
	}

	@Test
	@WithMockAdmin
	public void testBookHomeView_whenAuthenticated_shouldNotContainTheLoginForm() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		
		assertThat(bookHomeView.getElementById("login-form"))
			.isNull();
	}

	@Test
	public void testBookHomeView_whenUserDoNotFillTheUserAndPressTheSubmitButton_shouldNotSuccesfullyLogin() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		loginForm.getButtonByName("submit-button").click();

		assertThat(bookHomeView.getElementById("header").asText())
			.contains("Welcome to my book library!");

		verify(bookWebController, times(1))
			.getBookHomeView();
		verifyNoMoreInteractions(bookWebController);
	}

	@Test
	public void testBookHomeView_whenUserDoNotFillThePasswordAndPressTheSubmitButton_shouldNotSuccesfullyLogin() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getButtonByName("submit-button").click();
		
		assertThat(bookHomeView.getElementById("header").asText())
			.contains("Welcome to my book library!");
		
		verify(bookWebController, times(1))
			.getBookHomeView();
		verifyNoMoreInteractions(bookWebController);
	}

	@Test
	public void testBookHomeView_whenUserFillTheFormWithInvalidUserAndPressTheSubmitButton_shouldShowAuthenticationErrorMessage() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(INVALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		
		assertThat(bookHomeView.getElementById("authentication-error").asText())
			.isEqualTo("Invalid user name or password");
		
		verify(bookWebController, times(2))
			.getBookHomeView();
		verifyNoMoreInteractions(bookWebController);
	}

	@Test
	public void testBookHomeView_whenUserFillTheFormWithInvalidPasswordAndPressTheSubmitButton_shouldShowAuthenticationErrorMessage() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(INVALID_PASSWORD);
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		
		assertThat(bookHomeView.getElementById("authentication-error").asText())
			.isEqualTo("Invalid user name or password");
		
		verify(bookWebController, times(2))
			.getBookHomeView();
		verifyNoMoreInteractions(bookWebController);
	}

	@Test
	public void testBookHomeView_whenUserFillTheFormWithValidInputsAndPressTheSubmitButton_shouldSuccesfullyLogin() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		
		assertThat(bookHomeView.getElementById("header").asText())
			.contains("Welcome back!");
		verify(bookWebController, times(2))
			.getBookHomeView();
		verifyNoMoreInteractions(bookWebController);
	}

	/* ---------- BookHomeView logout message tests ---------- */

	@Test
	@WithMockAdmin
	public void testBookHomeView_afterLogoutRequest_shouldShowLogoutMessage() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		bookHomeView = bookHomeView.getFormByName("logout-form")
			.getButtonByName("logout-button")
			.click();
		
		assertThat(bookHomeView.getElementById("logout-message").asText())
			.isEqualTo("Logged out successfully");
	}

	/* ---------- BookHomeView remember-me check-box tests ---------- */

	@Test
	public void testBookHomeView_shouldContainRememberMeCheckboxInLoginForm() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		assertThat(loginForm.getInputByName("remember-me").getTypeAttribute())
			.isEqualTo("checkbox");
		assertThat(loginForm.getElementsByTagName("label").get(2).asText())
			.isEqualTo("Remember me");
	}

	@Test
	public void testBookHomeView_whenLoginWithoutRememberMe_shouldNotSaveRememberMeCookie() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		
		assertThat(webClient.getCookieManager().getCookie("spring-bookshelf-remember-me"))
			.isNull();
	}

	@Test
	public void testBookHomeView_whenFailedLoginWithRememberMe_shouldNotSaveRememberMeCookie() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(INVALID_PASSWORD);
		loginForm.getInputByName("remember-me").click();
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		assertThat(bookHomeView.getElementById("header").asText())
			.contains("Welcome to my book library");
		
		assertThat(webClient.getCookieManager().getCookie("spring-bookshelf-remember-me"))
			.isNull();
	}

	@Test
	public void testBookHomeView_whenSuccessfulLoginWithRememberMe_shouldSaveRememberMeCookie() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		loginForm.getInputByName("remember-me").click();
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		assertThat(bookHomeView.getElementById("header").asText())
			.contains("Welcome back!");
		
		assertThat(webClient.getCookieManager().getCookie("spring-bookshelf-remember-me"))
			.isNotNull();
	}

	@Test
	public void testBookHomeView_afterSuccessfulLoginWithRememberMe_cookieShouldExpireAfter30Minutes() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		loginForm.getInputByName("remember-me").click();
		loginForm.getButtonByName("submit-button").click();
		
		Date expires = webClient.getCookieManager()
			.getCookie("spring-bookshelf-remember-me")
			.getExpires();
		
		Calendar expectedExpires = Calendar.getInstance();
		expectedExpires.add(Calendar.MINUTE, 30);
		
		assertThat(expires.toInstant())
			.isCloseTo(expectedExpires.toInstant(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testBookHome_afterSuccessfulLoginWithRememberMe_logoutRequestshouldDeleteRememeberMeCookies() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm loginForm = bookHomeView.getFormByName("login-form");
		
		loginForm.getInputByName("username").setValueAttribute(VALID_USER_NAME);
		loginForm.getInputByName("password").setValueAttribute(VALID_PASSWORD);
		loginForm.getInputByName("remember-me").click();
		bookHomeView = loginForm.getButtonByName("submit-button").click();
		
		bookHomeView.getFormByName("logout-form")
			.getButtonByName("logout-button")
			.click();
		
		assertThat(webClient.getCookieManager().getCookie("spring-bookshelf-remember-me"))
			.isNull();
	}

	/* ---------- BookHomeView layout tests ---------- */

	@Test
	public void testBookHomeView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		
		assertThat(bookHomeView.getTitleText())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookHomeView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor homePageLink = bookHomeView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString()).contains("nav");
		assertThat(homePageLink.getHrefAttribute()).isEqualTo(URI_BOOK_HOME);
		verify(bookWebController, times(2))
			.getBookHomeView();
	}

	@Test
	public void testBookHomeView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor showBookListLink = bookHomeView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testBookHomeView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor searchBookByIsbnLink = bookHomeView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testBookHomeView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor searchBookByTitleLink = bookHomeView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testBookHomeView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		
		assertThatThrownBy(() -> bookHomeView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookHomeView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor addNewBookLink = bookHomeView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testBookHomeView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		
		assertThatThrownBy(() -> bookHomeView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookHomeView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlForm logoutForm = bookHomeView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController, times(2))
			.getBookHomeView();
	}

	@Test
	public void testBookHomeView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookHomeView = webClient.getPage(URI_BOOK_HOME);
		HtmlFooter footer = (HtmlFooter) bookHomeView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
