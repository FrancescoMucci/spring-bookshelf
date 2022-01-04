package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
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
import com.gargoylesoftware.htmlunit.html.HtmlTable;

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

	/* ---------- BookSearchByIsbnView search-by-ISBN form basic functionality tests ---------- */

	@Test
	public void testBookSearchByIsbnView_shouldAlwaysContainAFormToSearchBookByIsbn() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		
		assertThat(searchBookByIsbnForm.getElementsByTagName("label").get(0).asText())
			.isEqualTo("ISBN-13");
		assertThat(searchBookByIsbnForm.getInputByName("isbn").getPlaceholder())
			.isEqualTo("e.g. 9781401238964");
	}

	@Test
	public void testBookSearchByIsbnView_whenUserFillTheFormWithValidIsbnAndPressTheSubmitButton_shouldSendGetRequestToGetByIsbnEndpoint() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		searchBookByIsbnForm.getInputByName("isbn").setValueAttribute(VALID_ISBN13_WITH_HYPHENS);
		searchBookByIsbnForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.getBookByIsbn(eq(new BookData(VALID_ISBN13_WITH_HYPHENS, null, null)), any(BindingResult.class), any(Model.class));
	}

	@Test
	public void testBookSearchByIsbnView_whenUserFillTheFormWithInvalidIsbnAndPressTheSubmitButton_shouldSendGetRequestToGetByIsbnEndpoint() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		searchBookByIsbnForm.getInputByName("isbn").setValueAttribute(INVALID_ISBN13_WITH_HYPHENS);
		searchBookByIsbnForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.getBookByIsbn(eq(new BookData(INVALID_ISBN13_WITH_HYPHENS, null, null)), any(BindingResult.class), any(Model.class));
	}

	@Test
	public void testBookSearchByIsbnView_whenUserDoNotFillTheIsbnAndPressTheSubmitButton_shouldNotSendGetRequestToGetByIsbnEndpoint() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		searchBookByIsbnForm.getButtonByName("submit-button").click();

		verify(bookWebController, never())
			.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class));
	}

	/* ---------- BookSearchByIsbnView search-by-ISBN form pre-filling capabilities tests ---------- */

	@Test
	public void testBookSearchByIsbnView_afterGetRequestToGetByIsbnEndpointWithValidIsbn_theFormInputShouldBePrefilledWithProvidedIsbn() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
		.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITH_HYPHENS);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		
		assertThat(searchBookByIsbnForm.getInputByName("isbn").getValueAttribute())
		.isEqualTo(VALID_ISBN13_WITH_HYPHENS);
	}

	@Test
	public void testBookSearchByIsbnView_afterGetRequestToGetByIsbnEndpointWithInvalidIsbn_theFormInputShouldBePrefilledWithProvidedIsbn() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + INVALID_ISBN13_WITH_HYPHENS);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		
		assertThat(searchBookByIsbnForm.getInputByName("isbn").getValueAttribute())
			.isEqualTo(INVALID_ISBN13_WITH_HYPHENS);
	}

	@Test
	public void testBookSearchByIsbnView_whenJustOpened_theFormInputShouldNotBePrefilled() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		
		assertThat(searchBookByIsbnForm.getInputByName("isbn").getValueAttribute())
			.isEmpty();
	}

	/* ---------- BookSearchByIsbnView search-by-ISBN form validation error messages tests ---------- */

	@Test
	public void testBookSearchByIsbnView_afterGetRequestToGetByIsbnEndpointWithNullIsbn_shouldContainBlankValidationErrorMessage() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN);
		
		assertThat(bookSearchByIsbnView.getHtmlElementById("isbn-validation-error").asText())
			.isEqualTo("Please fill out this field");
	}

	@Test
	public void testBookSearchByIsbnView_afterGetRequestToGetByIsbnEndpointWithBlankIsbn_shouldContainIsbnValidationErrorMessage() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=");
		
		assertThat(bookSearchByIsbnView.getHtmlElementById("isbn-validation-error").asText())
			.isEqualTo("Invalid ISBN-13; check the advice box to understand how ISBN-13 works");
	}

	@Test
	public void testBookSearchByIsbnView_afterGetRequestToGetByIsbnEndpointWithInvalidIsbn_shouldContainIsbnValidationErrorMessage() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + INVALID_ISBN13_WITH_HYPHENS);
		
		assertThat(bookSearchByIsbnView.getHtmlElementById("isbn-validation-error").asText())
			.isEqualTo("Invalid ISBN-13; check the advice box to understand how ISBN-13 works");
	}

	@Test
	public void testBookSearchByIsbnView_whenUserFillTheFormWithInvalidIsbnButDoNotPressTheSubmitButton_shouldNotShowValidationErrorMessage() throws Exception {
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);;
		
			HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_SEARCH_BY_ISBN);
		HtmlForm searchBookByIsbnForm = bookSearchByIsbnView.getFormByName("search-book-by-isbn-form");
		searchBookByIsbnForm.getInputByName("isbn").setValueAttribute(INVALID_ISBN13_WITH_HYPHENS);
		
		assertThat(searchBookByIsbnForm.asText())
			.doesNotContain("Invalid ISBN-13; check the advice box to understand how ISBN-13 works");
	}

	/* ---------- BookSearchByIsbnView ISBN-advice-box tests ---------- */

	@Test
	public void testBookSearchByIsbnView_untilABookIsFound_shouldContainTheIsbnAdviceBox() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + INVALID_ISBN13_WITH_HYPHENS);
		
		assertThat(bookSearchByIsbnView.getElementById("advice-box").asText())
			.contains("Advice", "Understand how ISBN-13 works");
		assertThat(bookSearchByIsbnView.getAnchorByHref("https://www.isbn-international.org/content/what-isbn").asText())
			.isEqualTo("What is a ISBN-13?");
		assertThat(bookSearchByIsbnView.getAnchorByHref("https://en.wikipedia.org/wiki/International_Standard_Book_Number#ISBN-13_check_digit_calculation").asText())
			.isEqualTo("How ISBN-13 validation works?");
		assertThat(bookSearchByIsbnView.getAnchorByHref("https://isbndb.com/").asText())
			.isEqualTo("How do I find out the ISBN-13 of a book?");
	}

	@Test
	public void testBookSearchByIsbnView_whenABookIsFound_shouldNotContainTheIsbnAdviceBox() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITH_HYPHENS);
		
		assertThat(bookSearchByIsbnView.getElementById("advice-box"))
			.isNull();
		assertThat(bookSearchByIsbnView.asText())
			.doesNotContain("Advice", "Understand how ISBN-13 works");
	}

	/* ---------- BookSearchByIsbnView book table tests ---------- */

	@Test
	public void testBookSearchByIsbnView_untilABookIsFound_shouldNotContainTheBookTable() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + INVALID_ISBN13);
		
		assertThat(bookSearchByIsbnView.getElementById("book-table-fragment"))
			.isNull();
	}

	@Test
	public void testBookSearchByIsbnView_whenAnonymousUserAndABookIsFound_shouldContainTheFoundedBookInATableWithoutEditAndDeleteColumns() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITH_HYPHENS);
		HtmlTable bookTable = bookSearchByIsbnView.getHtmlElementById("book-table");
		
		assertThat(bookSearchByIsbnView.getElementById("book-table-fragment"))
			.isNotNull();
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(bookTable.asText()))
			.isEqualTo(
				"ISBN-13"      + "	" + "Title" + "	" + "Authors"      + "\n" +
				VALID_ISBN13 + "	" + TITLE + "	" + AUTHORS_LIST
		);
	}

	@Test
	@WithMockAdmin
	public void testBookSearchByIsbnView_whenAndminAndABookIsFound_shouldContainTheFoundedBookInATableWithEditAndDeleteColumns() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITH_HYPHENS);
		HtmlTable bookTable = bookSearchByIsbnView.getHtmlElementById("book-table");
		
		assertThat(bookSearchByIsbnView.getElementById("book-table-fragment"))
			.isNotNull();
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(bookTable.asText()))
			.isEqualTo(
				"ISBN-13"      + "	" + "Title" + "	" + "Authors"      + "	" + "Edit book" + "	" + "Delete book" + "\n" +
				VALID_ISBN13 + "	" + TITLE + "	" + AUTHORS_LIST + "	" + " Edit "    + "	" + "Delete"
		);
	}

	@Test
	public void testBookSearchByIsbnView_whenAnonymousUserAndABookIsFound_shouldNotProvideAnEditLinkForTheBookInTheTable() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITHOUT_FORMATTING);
		
		assertThatThrownBy(() -> bookSearchByIsbnView.getAnchorByText("Edit"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookSearchByIsbnView_whenAdminAndABookIsFound_shouldProvideAnEditLinkForTheBookInTheTable() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITHOUT_FORMATTING);
		bookSearchByIsbnView.getAnchorByText("Edit").click();
		
		verify(bookWebController)
			.getBookEditView(
				eq(new IsbnData(VALID_ISBN13_WITHOUT_FORMATTING)),
				any(BindingResult.class),
				eq(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, null, null)));
	}

	@Test
	public void testBookSearchByIsbnView_whenAnonymoudUserAndABookIsFound_shouldNotProvideADeleteDialogForTheBookInTheTable() throws Exception {
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		
		List<String> dialogContent = asList(
				"Do you really want to delete this book?",
				VALID_ISBN13 + " - " + TITLE + " - " + AUTHORS_LIST,
				"No", "Yes, delete"
		);
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITHOUT_FORMATTING);
		
		assertThat(bookSearchByIsbnView.getElementById("getDeleteBookDialogButton-" + VALID_ISBN13))
			.isNull();
		assertThat(bookSearchByIsbnView.getElementById("deleteBookDialog-" + VALID_ISBN13))
			.isNull();
		assertThat(bookSearchByIsbnView.asText())
			.doesNotContain(dialogContent);
	}

	@Test
	@WithMockAdmin
	public void testBookSearchByIsbnView_whenAdminAndABookIsFound_shouldProvideADeleteDialogForTheBookInTheTable() throws Exception {
		List<String> dialogContent = asList(
				"Do you really want to delete this book?",
				VALID_ISBN13 + " - " + TITLE + " - " + AUTHORS_LIST,
				"No", "Yes, delete"
		);
		when(bookWebController.getBookByIsbn(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
				return VIEW_BOOK_SEARCH_BY_ISBN;
			}
		));
		when(bookWebController.postDeleteBook(any(IsbnData.class), any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookSearchByIsbnView = webClient.getPage(URI_BOOK_GET_BY_ISBN + "?isbn=" + VALID_ISBN13_WITHOUT_FORMATTING);
		assertThat(bookSearchByIsbnView.asText())
			.doesNotContain(dialogContent);

		bookSearchByIsbnView.getHtmlElementById("getDeleteBookDialogButton-" + VALID_ISBN13).click();
		webClient.waitForBackgroundJavaScript(1000);
		assertThat(bookSearchByIsbnView.asText())
			.contains(dialogContent);
		
		bookSearchByIsbnView.getHtmlElementById("deleteBookDialog-" + VALID_ISBN13 + "-closeButton").click();
		webClient.waitForBackgroundJavaScript(1000);
		assertThat(bookSearchByIsbnView.asText())
			.doesNotContain(dialogContent);
		
		bookSearchByIsbnView.getHtmlElementById("getDeleteBookDialogButton-" + VALID_ISBN13).click();
		webClient.waitForBackgroundJavaScript(1000);
		bookSearchByIsbnView.getHtmlElementById("deleteBookDialog-" + VALID_ISBN13 + "-noButton").click();
		webClient.waitForBackgroundJavaScript(1000);
		assertThat(bookSearchByIsbnView.asText())
			.doesNotContain(dialogContent);
		
		bookSearchByIsbnView.getHtmlElementById("getDeleteBookDialogButton-" + VALID_ISBN13).click();
		webClient.waitForBackgroundJavaScript(1000);
		bookSearchByIsbnView.getHtmlElementById("deleteBookDialog-" + VALID_ISBN13 + "-yesButton").click();
		verify(bookWebController)
			.postDeleteBook(eq(new IsbnData(VALID_ISBN13_WITHOUT_FORMATTING)), any(BindingResult.class));
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
