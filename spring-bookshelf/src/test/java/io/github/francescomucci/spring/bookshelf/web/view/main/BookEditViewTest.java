package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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

import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.web.BookWebController;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.view.BookViewTestingHelperMethods;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
@WithMockAdmin
public class BookEditViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
	}

	/* ---------- BookEditView header message tests ---------- */

	@Test
	public void testBookEditView_shouldAlwaysContainAnInformativeTextInTheHeader() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlHeader header = (HtmlHeader) bookEditView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Book edit form" + "\n" + 
				"Edit book title and authors");
	}

	/* ---------- BookEditView edit form basic functionality tests ---------- */

	@Test
	public void testBookEditView_shouldAlwaysContainAFormToEditExistingBook() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		
		assertThat(editBookForm.getInputByName("isbn").getAttribute("type"))
			.isEqualTo("hidden");
		
		assertThat(editBookForm.getElementsByTagName("label").get(0).asText())
			.isEqualTo("Title");
		assertThat(editBookForm.getInputByName("title").getPlaceholder())
			.isEqualTo("e.g. Watchmen");
		
		assertThat(editBookForm.getElementsByTagName("label").get(1).asText())
			.isEqualTo("Authors");
		assertThat(editBookForm.getInputByName("authors").getPlaceholder())
			.isEqualTo("e.g. Alan Moore, Dave Gibbons");
		
		assertThat(editBookForm.getButtonByName("submit-button").asText())
			.isEqualTo("Save edit");
	}

	@Test
	public void testBookEditView_whenUserFillTheFormWithValidInputsAndPressTheSubmitButton_shouldSendPostRequestToSaveEndpoint() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(NEW_TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(AUTHORS_STRING);
		editBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.postSaveBook(eq(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, NEW_TITLE, AUTHORS_STRING)), any(BindingResult.class));
	}

	@Test
	public void testBookEditView_whenUserFillTheFormWithInvalidInputsAndPressTheSubmitButton_shouldSendPostRequestToSaveEndpoint() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(INVALID_AUTHORS_STRING);
		editBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.postSaveBook(eq(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, INVALID_TITLE, INVALID_AUTHORS_STRING)), any(BindingResult.class));
	}

	@Test
	public void testBookEditView_whenUserDoNotFillTheTitleAndPressTheSubmitButton_shouldNotSendPostRequestToSaveEndpoint() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute("");
		editBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController, never())
			.postSaveBook(any(BookData.class), any(BindingResult.class));
	}

	@Test
	public void testBookEditView_whenUserDoNotFillTheAuthorsAndPressTheSubmitButton_shouldNotSendPostRequestToSaveEndpoint() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("authors").setValueAttribute("");
		editBookForm.getButtonByName("submit-button").click();
		
		verify(bookWebController, never())
			.postSaveBook(any(BookData.class), any(BindingResult.class));
	}

	/* ---------- BookEditView layout tests ---------- */

	@Test
	public void testBookEditView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
	
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
	
		assertThat(bookEditView.getTitleText())
			.isEqualTo("Book edit view");
	}

	@Test
	public void testBookEditView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlAnchor homePageLink = bookEditView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString())
			.contains("nav");
		assertThat(homePageLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookEditView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlAnchor showBookListLink = bookEditView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testBookEditView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlAnchor searchBookByIsbnLink = bookEditView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testBookEditView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlAnchor searchBookByTitleLink = bookEditView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testBookEditView_shouldAlwaysProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlAnchor addNewBookLink = bookEditView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testBookEditView_shouldAlwaysProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm logoutForm = bookEditView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookEditView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlFooter footer = (HtmlFooter) bookEditView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}
}
