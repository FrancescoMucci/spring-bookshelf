package io.github.francescomucci.spring.bookshelf.web.view.error;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
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
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.web.BookWebController;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.view.BookViewTestingHelperMethods;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
public class BookNotFoundViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getCookieManager().clearCookies();
	}

	/* ---------- BookNotFoundView header message tests ---------- */

	@Test
	public void testBookNotFoundView_shouldAlwaysContainAWarningMessageInTheHeader() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		HtmlHeader header = (HtmlHeader) bookNotFoundView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Book not found" + "\n" + 
				"The inserted ISBN-13 or title is not associated with any book in the database!");
	}

	/* ---------- BookNotFoundView error-info-box tests ---------- */

	@Test
	public void testBookNotFoundView_shouldAlwaysContainTheErrorInfoBox_caseSearchByIsbn() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData bookFormData, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value());
				model.addAttribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase());
				model.addAttribute(MODEL_ERROR_MESSAGE, UNUSED_ISBN13 + BookNotFoundException.BOOK_NOT_FOUND_MSG);
				return ERROR_BOOK_NOT_FOUND;
			}
		));
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		
		assertThat(bookNotFoundView.getElementById("error-info-box").asText())
			.contains(
				"Status", "404",
				"Error", "Not Found",
				"Message", UNUSED_ISBN13 + ": no book found with this ISBN-13");
	}

	@Test
	public void testBookNotFoundView_shouldAlwaysContainTheErrorInfoBox_caseSearchByTitle() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData bookFormData, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value());
				model.addAttribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase());
				model.addAttribute(MODEL_ERROR_MESSAGE, UNUSED_TITLE + BookNotFoundException.BOOK_NOT_FOUND_TITLE_MSG);
				return ERROR_BOOK_NOT_FOUND;
			}
		));
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + UNUSED_TITLE);

		assertThat(bookNotFoundView.getElementById("error-info-box").asText())
			.contains(
				"Status", "404",
				"Error", "Not Found",
				"Message", UNUSED_TITLE + ": no book found with this title");
	}

	/* ---------- BookNotFoundView advice-box tests ---------- */

	@Test
	public void testBookNotFoundView_shouldAlwaysContainTheBookNotFoundAdviceBox() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
	
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		
		assertThat(bookNotFoundView.getElementById("advice-box").asText())
			.contains(
				"Advice",
				"Make sure you typed the correct ISBN-13 or title",
				"If you are sure of what you typed, we are sorry to inform you that the book you are looking for is not present in our database");
	}

	/* ---------- BookNotFoundView layout tests ---------- */

	@Test
	public void testBookNotFoundView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);

		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		
		assertThat(bookNotFoundView.getTitleText())
			.isEqualTo("Book not found error view");
	}

	@Test
	public void testBookNotFoundView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		HtmlAnchor homePageLink = bookNotFoundView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString()).contains("nav");
		assertThat(homePageLink.getHrefAttribute()).isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookNotFoundView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		HtmlAnchor showBookListLink = bookNotFoundView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController)
			.getBookListView(any(Model.class));
	}

	@Test
	public void testBookNotFoundView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		HtmlAnchor searchBookByIsbnLink = bookNotFoundView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testBookNotFoundView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		HtmlAnchor searchBookByTitleLink = bookNotFoundView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testBookNotFoundView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		
		assertThatThrownBy(() -> bookNotFoundView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookNotFoundView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookNotFoundView = webClient.getPage("/book/edit/" + UNUSED_ISBN13);
		HtmlAnchor addNewBookLink = bookNotFoundView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testBookNotFoundView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);

		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		
		assertThatThrownBy(() -> bookNotFoundView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookNotFoundView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookNotFoundView = webClient.getPage("/book/edit/" + UNUSED_ISBN13);
		HtmlForm logoutForm = bookNotFoundView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookNotFoundView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(ERROR_BOOK_NOT_FOUND);
	
		HtmlPage bookNotFoundView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + UNUSED_ISBN13);
		HtmlFooter footer = (HtmlFooter) bookNotFoundView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
