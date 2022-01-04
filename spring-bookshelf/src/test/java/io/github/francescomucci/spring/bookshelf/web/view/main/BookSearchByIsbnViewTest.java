package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.web.BookWebController;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.view.BookViewTestingHelperMethods;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlFooter;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeader;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
public class BookSearchByIsbnViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getCookieManager().clearCookies();
	}

	/* ---------- BookSearchByIsbnView header message tests ---------- */

	@Test
	public void testBookSearchByIsbnView_shouldAlwaysContainAnInformativeTextInTheHeader() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlHeader header = (HtmlHeader) bookSearchByIsbnView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Book search by ISBN form" + "\n" + 
				"Insert the ISBN-13 of the book you want to retrieve from the database");
	}

	/* ---------- BookSearchByIsbnView layout tests ---------- */

	@Test
	public void testBookSearchByIsbnViewView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
	
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
	
		assertThat(bookSearchByIsbnView.getTitleText())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testSearchByIsbnView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlAnchor homePageLink = bookSearchByIsbnView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString())
			.contains("nav");
		assertThat(homePageLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testSearchByIsbnView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlAnchor showBookListLink = bookSearchByIsbnView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testSearchByIsbnView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlAnchor searchBookByIsbnLink = bookSearchByIsbnView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController, times(2))
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testSearchByIsbnView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlAnchor searchBookByTitleLink = bookSearchByIsbnView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testSearchByIsbnView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		
		assertThatThrownBy(() -> bookSearchByIsbnView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testSearchByIsbnView_whenAdmin_shouldAlwaysProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlAnchor addNewBookLink = bookSearchByIsbnView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testSearchByIsbnView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		
		assertThatThrownBy(() -> bookSearchByIsbnView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testSearchByIsbnView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm logoutForm = bookSearchByIsbnView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testSearchByIsbnView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlFooter footer = (HtmlFooter) bookSearchByIsbnView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
