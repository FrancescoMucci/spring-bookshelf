package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlFooter;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHeader;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

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

	/* ---------- BookEditView edit form pre-filling capabilities tests ---------- */

	@Test
	public void testBookEditView_whenJustOpened_theFormInputsShouldBePrefilled() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenAnswer(answer((IsbnData isbn, BindingResult result, BookData editFormData)-> {
				editFormData.setTitle(TITLE);
				editFormData.setAuthors(AUTHORS_STRING);
				return VIEW_BOOK_EDIT;
			}
		));
	
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		
		assertThat(editBookForm.getInputByName("isbn").getValueAttribute())
			.isEqualTo(VALID_ISBN13_WITHOUT_FORMATTING);
		assertThat(editBookForm.getInputByName("title").getValueAttribute())
			.isEqualTo(TITLE);
		assertThat(editBookForm.getInputByName("authors").getValueAttribute())
			.isEqualTo(AUTHORS_STRING);
	}

	@Test
	public void testBookEditView_afterPostRequestToSaveEditedBookWithSomeInvalidInput_theFormInputsShouldBePrefilledWithProvidedInputs() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(INVALID_AUTHORS_STRING);
		bookEditView = editBookForm.getButtonByName("submit-button").click();
		editBookForm = bookEditView.getFormByName("edit-book-form");
		
		assertThat(editBookForm.getInputByName("isbn").getValueAttribute())
			.isEqualTo(VALID_ISBN13_WITHOUT_FORMATTING);
		assertThat(editBookForm.getInputByName("title").getValueAttribute())
			.isEqualTo(INVALID_TITLE);
		assertThat(editBookForm.getInputByName("authors").getValueAttribute())
			.isEqualTo(INVALID_AUTHORS_STRING);
	}

	/* ---------- BookEditView edit form validation error messages tests ---------- */

	@Test
	public void testBookEditView_afterPostRequestToSaveEditedBookWithInvalidTitle_shouldContainTitleValidationErrorMessage() throws Exception {
		String invalidTitleMessage = "Invalid title; the allowed special characters are: & , : . ! ?";
		String invalidAuthorsMessage = "Invalid authors; numbers and all special special characters, except the comma, are not allowed";
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(AUTHORS_STRING);
		bookEditView = editBookForm.getButtonByName("submit-button").click();
		editBookForm = bookEditView.getFormByName("edit-book-form");
		
		assertThat(editBookForm.asText())
			.doesNotContain(invalidAuthorsMessage)
			.contains(invalidTitleMessage);
	}

	@Test
	public void testBookEditView_afterPostRequestToSaveEditedBookWithInvalidAuthors_shouldContainAuthorsValidationErrorMessage() throws Exception {
		String invalidTitleMessage = "Invalid title; the allowed special characters are: & , : . ! ?";
		String invalidAuthorsMessage = "Invalid authors; numbers and all special special characters, except the comma, are not allowed";
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(INVALID_AUTHORS_STRING);
		bookEditView = editBookForm.getButtonByName("submit-button").click();
		editBookForm = bookEditView.getFormByName("edit-book-form");
		
		assertThat(editBookForm.asText())
			.doesNotContain(invalidTitleMessage)
			.contains(invalidAuthorsMessage);
	}

	@Test
	public void testBookEditView_afterPostRequestToSaveEditedBookWithInvalidTitleAndAuthors_shouldContainBothValidationErrorMessages() throws Exception {
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(INVALID_AUTHORS_STRING);
		bookEditView = editBookForm.getButtonByName("submit-button").click();
		editBookForm = bookEditView.getFormByName("edit-book-form");
		
		assertThat(bookEditView.getHtmlElementById("title-validation-error").asText())
			.isEqualTo("Invalid title; the allowed special characters are: & , : . ! ?");
		assertThat(bookEditView.getHtmlElementById("authors-validation-error").asText())
			.isEqualTo("Invalid authors; numbers and all special special characters, except the comma, are not allowed");
	}

	@Test
	public void testBookEditView_afterPostRequestToSaveEditedBookWithBlankTitleAndAuthors_shouldContainBlankValidationErrorMessages() throws Exception {
		String blankFieldMessage = "Please fill out this field";
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		WebRequest postRequestToSaveBook = new WebRequest(
			new URL("http://localhost:8080" + URI_BOOK_SAVE), HttpMethod.POST);
		postRequestToSaveBook.setRequestParameters(asList(
			new NameValuePair("_csrf", editBookForm.getInputByName("_csrf").getValueAttribute()),
			new NameValuePair("isbn", VALID_ISBN13_WITHOUT_FORMATTING),
			new NameValuePair("title", ""),
			new NameValuePair("authors", "")));
		bookEditView = webClient.getPage(postRequestToSaveBook);
		
		assertThat(bookEditView.getHtmlElementById("title-validation-error").asText())
			.isEqualTo(blankFieldMessage);
		assertThat(bookEditView.getHtmlElementById("authors-validation-error").asText())
			.isEqualTo(blankFieldMessage);
	}

	@Test
	public void testBookEditView_afterPostRequestToSaveEditedBookWithNullTitleAndAuthors_shouldContainBlankValidationErrorMessages() throws Exception {
		String blankFieldMessage = "Please fill out this field";
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		when(bookWebController.postSaveBook(any(BookData.class),any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		WebRequest postRequestToSaveBook = new WebRequest(
			new URL("http://localhost:8080" + URI_BOOK_SAVE), HttpMethod.POST);
		postRequestToSaveBook.setRequestParameters(asList(
			new NameValuePair("_csrf", editBookForm.getInputByName("_csrf").getValueAttribute()),
			new NameValuePair("isbn", VALID_ISBN13_WITHOUT_FORMATTING),
			new NameValuePair("title", null),
			new NameValuePair("authors", null)));
		bookEditView = webClient.getPage(postRequestToSaveBook);
		
		assertThat(bookEditView.getHtmlElementById("title-validation-error").asText())
			.isEqualTo(blankFieldMessage);
		assertThat(bookEditView.getHtmlElementById("authors-validation-error").asText())
			.isEqualTo(blankFieldMessage);
	}

	@Test
	public void testBookEditView_whenUserFillTheFormWithInvalidInputsButDoNotPressTheSubmitButton_shouldNotShowValidationErrorMessages() throws Exception {
		String invalidTitleMessage = "Invalid title; the allowed special characters are: & , : . ! ?";
		String invalidAuthorsMessage = "Invalid authors; numbers and all special special characters, except the comma, are not allowed";
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookEditView = webClient.getPage("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING);
		HtmlForm editBookForm = bookEditView.getFormByName("edit-book-form");
		editBookForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		editBookForm.getInputByName("authors").setValueAttribute(INVALID_AUTHORS_STRING);
		
		assertThat(editBookForm.asText())
			.doesNotContain(invalidTitleMessage, invalidAuthorsMessage);
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
