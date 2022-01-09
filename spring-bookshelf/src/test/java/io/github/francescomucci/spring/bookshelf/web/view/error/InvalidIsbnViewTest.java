package io.github.francescomucci.spring.bookshelf.web.view.error;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlFooter;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeader;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.web.BookWebController;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.view.BookViewTestingHelperMethods;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
@WithMockAdmin
public class InvalidIsbnViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
	}

	/* ---------- InvalidIsbnView header message tests ---------- */

	@Test
	public void testInvalidIsbnView_shouldAlwaysContainAWarningMessageInTheHeader() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlHeader header = (HtmlHeader) invalidIsbnView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Invalid ISBN-13" + "\n" + 
				"The inserted ISBN-13 do not have passed the validation process!");
	}

	/* ---------- InvalidIsbnView layout tests ---------- */

	@Test
	public void testInvalidIsbnView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);

		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		
		assertThat(invalidIsbnView.getTitleText())
			.isEqualTo("Invalid ISBN-13 error view");
	}

	@Test
	public void testInvalidIsbnView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlAnchor homePageLink = invalidIsbnView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString()).contains("nav");
		assertThat(homePageLink.getHrefAttribute()).isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testInvalidIsbnView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlAnchor showBookListLink = invalidIsbnView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testInvalidIsbnView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlAnchor searchBookByIsbnLink = invalidIsbnView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testInvalidIsbnView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlAnchor searchBookByTitleLink = invalidIsbnView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	@WithAnonymousUser
	public void testInvalidIsbnView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' and 'getBookEditView' with an already used isbn should throw InvalidIsbnException.
		 * Normally an anonymous user should not be able to access InvalidIsbnView */
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_INVALID_ISBN);
		
		HtmlPage invalidIsbnView = webClient.getPage(URI_BOOK_HOME);
		
		assertThatThrownBy(() -> invalidIsbnView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	public void testInvalidIsbnView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlAnchor addNewBookLink = invalidIsbnView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	@WithAnonymousUser
	public void testInvalidIsbnView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' and 'getBookEditView' with an already used isbn should throw InvalidIsbnException.
		 * Normally an anonymous user should not be able to access InvalidIsbnView */
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_INVALID_ISBN);
		
		HtmlPage invalidIsbnView = webClient.getPage(URI_BOOK_HOME);
		
		assertThatThrownBy(() -> invalidIsbnView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	public void testInvalidIsbnView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlForm logoutForm = invalidIsbnView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testInvalidIsbnView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_INVALID_ISBN);
		
		HtmlPage invalidIsbnView = webClient.getPage("/book/edit/" + INVALID_ISBN13);
		HtmlFooter footer = (HtmlFooter) invalidIsbnView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
