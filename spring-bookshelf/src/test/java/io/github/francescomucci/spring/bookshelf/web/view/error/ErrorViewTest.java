package io.github.francescomucci.spring.bookshelf.web.view.error;

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
public class ErrorViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getCookieManager().clearCookies();
	}

	/* ---------- ErrorView header message tests ---------- */

	@Test
	public void testErrorView_shouldAlwaysContainAWarningMessageInTheHeader() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlHeader header = (HtmlHeader) bookErrorView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Something went wrong" + "\n" + 
				"We do not know what but we are going to work on it!");
	}

	/* ---------- ErrorView layout tests ---------- */

	@Test
	public void testErrorView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");

		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		
		assertThat(bookErrorView.getTitleText())
			.isEqualTo("Unknown error view");
	}

	@Test
	public void testErrorView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor homePageLink = bookErrorView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString()).contains("nav");
		assertThat(homePageLink.getHrefAttribute()).isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testErrorView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor showBookListLink = bookErrorView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController, times(2))
			.getBookListView(any(Model.class));
	}

	@Test
	public void testErrorView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor searchBookByIsbnLink = bookErrorView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testErrorView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor searchBookByTitleLink = bookErrorView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testErrorView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		
		assertThatThrownBy(() -> bookErrorView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testErrorView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor addNewBookLink = bookErrorView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testErrorView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");

		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		
		assertThatThrownBy(() -> bookErrorView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testErrorView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlForm logoutForm = bookErrorView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testErrorView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn("error");
		
		HtmlPage bookErrorView = webClient.getPage(URI_BOOK_LIST);
		HtmlFooter footer = (HtmlFooter) bookErrorView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
