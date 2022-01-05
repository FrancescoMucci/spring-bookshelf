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
public class BookSearchByTitleViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getCookieManager().clearCookies();
	}

	/* ---------- BookSearchByTitleView header message tests ---------- */

	@Test
	public void testBookSearchByTitleView_shouldAlwaysContainAnInformativeTextInTheHeader() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlHeader header = (HtmlHeader) bookSearchByTitleView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Book search by title form" + "\n" +
				"Insert the title of the book you want to retrieve from the database");
	}

	/* ---------- BookSearchByTitleView layout tests ---------- */

	@Test
	public void testBookSearchByTitleViewView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		
		assertThat(bookSearchByTitleView.getTitleText())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testSearchByTitleView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlAnchor homePageLink = bookSearchByTitleView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString())
			.contains("nav");
		assertThat(homePageLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testSearchByTitleView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlAnchor showBookListLink = bookSearchByTitleView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testSearchByTitleView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlAnchor searchBookByIsbnLink = bookSearchByTitleView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testSearchByTitleView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlAnchor searchBookByTitleLink = bookSearchByTitleView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController, times(2))
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testSearchByTitleView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		
		assertThatThrownBy(() -> bookSearchByTitleView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testSearchByTitleView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlAnchor addNewBookLink = bookSearchByTitleView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testSearchByTitleView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		
		assertThatThrownBy(() -> bookSearchByTitleView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testSearchByTitleView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm logoutForm = bookSearchByTitleView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testSearchByTitleView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlFooter footer = (HtmlFooter) bookSearchByTitleView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
