package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import org.springframework.validation.BindingResult;

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
@WithMockAdmin
public class BookNewViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
	}

	/* ---------- BookNewView header message tests ---------- */

	@Test
	public void testBookNewView_shouldAlwaysContainAnInformativeTextInTheHeader() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlHeader header = (HtmlHeader) bookNewView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
		.isEqualTo(
			"Book new form" + "\n" + 
			"Insert book data to create a new book");
	}

	/* ---------- BookNewView new form basic functionality tests ---------- */

	@Test
	public void testBookNewView_shouldAlwaysContainAFormToAddNewBook() throws Exception {
		when(bookWebController.getBookNewView(new BookData()))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm newBookForm = bookNewView.getFormByName("new-book-form");
		
		assertThat(newBookForm.getElementsByTagName("label").get(0).asText())
			.isEqualTo("ISBN-13");
		assertThat(newBookForm.getInputByName("isbn").getPlaceholder())
			.isEqualTo("e.g. 9781401238964");
		
		assertThat(newBookForm.getElementsByTagName("label").get(1).asText())
			.isEqualTo("Title");
		assertThat(newBookForm.getInputByName("title").getPlaceholder())
			.isEqualTo("e.g. Watchmen");
		
		assertThat(newBookForm.getElementsByTagName("label").get(2).asText())
			.isEqualTo("Authors");
		assertThat(newBookForm.getInputByName("authors").getPlaceholder())
			.isEqualTo("e.g. Alan Moore, Dave Gibbons");
		
		assertThat(newBookForm.getButtonByName("submit-button").asText())
			.isEqualTo("Add book");
	}

	@Test
	public void testBookNewView_whenUserFillTheFormWithValidInputsAndPressTheSubmitButton_shouldSendPostRequestToAddEndpoint() throws Exception {
		BookData bookFormData = new BookData(VALID_ISBN13_WITH_HYPHENS, TITLE, AUTHORS_STRING);
		when(bookWebController.getBookNewView(new BookData()))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.postAddBook(any(BookData.class), any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm newBookForm = bookNewView.getFormByName("new-book-form");
		newBookForm.getInputByName("isbn").setValueAttribute(bookFormData.getIsbn());
		newBookForm.getInputByName("title").setValueAttribute(bookFormData.getTitle());
		newBookForm.getInputByName("authors").setValueAttribute(bookFormData.getAuthors());
		newBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.postAddBook(eq(bookFormData), any(BindingResult.class));
	}

	@Test
	public void testBookNewView_whenUserFillTheFormWithInvalidInputsAndPressTheSubmitButton_shouldSendPostRequestToAddEndpoint() throws Exception {
		BookData bookFormData = new BookData(INVALID_ISBN13_WITH_HYPHENS, INVALID_TITLE, INVALID_AUTHORS_STRING);
		when(bookWebController.getBookNewView(new BookData()))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.postAddBook(any(BookData.class), any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm newBookForm = bookNewView.getFormByName("new-book-form");
		newBookForm.getInputByName("isbn").setValueAttribute(bookFormData.getIsbn());
		newBookForm.getInputByName("title").setValueAttribute(bookFormData.getTitle());
		newBookForm.getInputByName("authors").setValueAttribute(bookFormData.getAuthors());
		newBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.postAddBook(eq(bookFormData), any(BindingResult.class));
	}

	@Test
	public void testBookNewView_whenUserDoNotFillTheIsbnAndPressTheSubmitButton_shouldNotSendPostRequestToAddEndpoint() throws Exception {
		when(bookWebController.getBookNewView(new BookData()))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm newBookForm = bookNewView.getFormByName("new-book-form");
		newBookForm.getInputByName("title").setValueAttribute(TITLE);
		newBookForm.getInputByName("authors").setValueAttribute(AUTHORS_STRING);
		newBookForm.getButtonByName("submit-button").click();

		verify(bookWebController, never())
			.postAddBook(any(BookData.class), any(BindingResult.class));
	}

	@Test
	public void testBookNewView_whenUserDoNotFillTheTitleAndPressTheSubmitButton_shouldNotSendPostRequestToAddEndpoint() throws Exception {
		when(bookWebController.getBookNewView(new BookData()))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm newBookForm = bookNewView.getFormByName("new-book-form");
		newBookForm.getInputByName("isbn").setValueAttribute(VALID_ISBN13_WITH_HYPHENS);
		newBookForm.getInputByName("authors").setValueAttribute(AUTHORS_STRING);
		newBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController, never())
			.postAddBook(any(BookData.class), any(BindingResult.class));
	}

	@Test
	public void testBookNewView_whenUserDoNotFillTheAuthorsAndPressTheSubmitButton_shouldNotSendPostRequestToAddEndpoint() throws Exception {
		when(bookWebController.getBookNewView(new BookData()))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm newBookForm = bookNewView.getFormByName("new-book-form");
		newBookForm.getInputByName("isbn").setValueAttribute(VALID_ISBN13_WITH_HYPHENS);
		newBookForm.getInputByName("title").setValueAttribute(TITLE);
		newBookForm.getButtonByName("submit-button").click();

		verify(bookWebController, never())
			.postAddBook(any(BookData.class), any(BindingResult.class));
	}


	/* ---------- BookNewView layout tests ---------- */

	@Test
	public void testBookNewView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
	
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
	
		assertThat(bookNewView.getTitleText()).isEqualTo("Book new view");
	}

	@Test
	public void testBookNewView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlAnchor homePageLink = bookNewView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString())
			.contains("nav");
		assertThat(homePageLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookNewView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlAnchor showBookListLink = bookNewView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testBookNewView_shouldAlwaysProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlAnchor addNewBookLink = bookNewView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController, times(2))
			.getBookNewView(new BookData());
	}

	@Test
	public void testBookNewView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlAnchor searchBookByIsbnLink = bookNewView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testBookNewView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlAnchor searchBookByTitleLink = bookNewView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testBookNewView_shouldAlwaysProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlForm logoutForm = bookNewView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookNewView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNewView = webClient.getPage(URI_BOOK_NEW);
		HtmlFooter footer = (HtmlFooter) bookNewView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
