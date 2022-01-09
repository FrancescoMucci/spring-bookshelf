package io.github.francescomucci.spring.bookshelf.web.view.error;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
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
import io.github.francescomucci.spring.bookshelf.exception.BookAlreadyExistException;
import io.github.francescomucci.spring.bookshelf.web.BookWebController;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.view.BookViewTestingHelperMethods;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
@WithMockAdmin
public class BookAlreadyExistViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
	}

	/* ---------- BookAlreadyExistView header message tests ---------- */

	@Test
	public void testBookAlreadyExistView_shouldAlwaysContainAWarningMessageInTheHeader() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlHeader header = (HtmlHeader) bookAlreadyExistView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Book already exist" + "\n" + 
				"The inserted ISBN-13 is already associated with a book in the database!");
	}

	/* ---------- BookAlreadyExistView error-info-box tests ---------- */

	@Test
	public void testBookAlreadyExistView_shouldAlwaysContainTheErrorInfoBox() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only for an easy setting of the model attributes.*/
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_ERROR_CODE, HttpStatus.CONFLICT.value());
				model.addAttribute(MODEL_ERROR_REASON, HttpStatus.CONFLICT.getReasonPhrase());
				model.addAttribute(MODEL_ERROR_MESSAGE, ALREADY_USED_ISBN13 + BookAlreadyExistException.BOOK_ALREADY_EXIST_MSG);
				return ERROR_BOOK_ALREADY_EXIST;
			}
		));
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_LIST);
		
		assertThat(bookAlreadyExistView.getElementById("error-info-box").asText())
			.contains(
				"Status", "409",
				"Error", "Conflict",
				"Message", ALREADY_USED_ISBN13 + ": a book with this ISBN-13 already exist");
	}

	/* ---------- BookAlreadyExistView layout tests ---------- */

	@Test
	public void testBookAlreadyExistView_shouldAlwaysHaveATitle() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);

		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		
		assertThat(bookAlreadyExistView.getTitleText())
			.isEqualTo("Book already exist error view");
	}

	@Test
	public void testBookAlreadyExistView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor homePageLink = bookAlreadyExistView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString()).contains("nav");
		assertThat(homePageLink.getHrefAttribute()).isEqualTo(URI_BOOK_HOME);
		verify(bookWebController, times(2))
			.getBookHomeView();
	}

	@Test
	public void testBookAlreadyExistView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor showBookListLink = bookAlreadyExistView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testBookAlreadyExistView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor searchBookByIsbnLink = bookAlreadyExistView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testBookAlreadyExistView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor searchBookByTitleLink = bookAlreadyExistView.getAnchorByText("Search book by title");
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
	public void testBookAlreadyExistView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'
		 * Normally an anonymous user should not be able to access BookAlreadyExistView */
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		
		assertThatThrownBy(() -> bookAlreadyExistView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	public void testBookAlreadyExistView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlAnchor addNewBookLink = bookAlreadyExistView.getAnchorByText("Add new book");
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
	public void testBookAlreadyExistView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postDeleteBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'
		 * Normally an anonymous user should not be able to access BookAlreadyExistView */
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		
		assertThatThrownBy(() -> bookAlreadyExistView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	public void testBookAlreadyExistView_whenAdmin_shouldProvideAButtonToLogout() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST)
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		
		HtmlForm logoutForm = bookAlreadyExistView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController, times(2))
			.getBookHomeView();
	}

	@Test
	public void testBookAlreadyExistView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		/* This stubbing do not represent a real case scenario: 
		 * only 'postAddBook' with an already used isbn parameter should throw BookAlreadyExistException.
		 * This was done only to avoid the tedious step of sending post request to '/book/add'*/
		when(bookWebController.getBookHomeView())
			.thenReturn(ERROR_BOOK_ALREADY_EXIST);
		
		HtmlPage bookAlreadyExistView = webClient.getPage(URI_BOOK_HOME);
		HtmlFooter footer = (HtmlFooter) bookAlreadyExistView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
