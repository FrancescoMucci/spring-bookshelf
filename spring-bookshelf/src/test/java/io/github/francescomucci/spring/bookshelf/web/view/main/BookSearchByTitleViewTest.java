package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
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

	/* ---------- BookSearchByTitleView search-by-title form basic functionality tests ---------- */

	@Test
	public void testBookSearchByTitleView_shouldAlwaysContainAFormToSearchBookByTitle() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		
		assertThat(searchBookByTitleForm.getElementsByTagName("label").get(0).asText())
			.isEqualTo("Title");
		assertThat(searchBookByTitleForm.getInputByName("title").getPlaceholder())
			.isEqualTo("e.g. Foundation");
	}
	@Test
	public void testBookSearchByTitleView_whenUserFillTheFormWithValidTitleAndPressTheSubmitButton_shouldSendGetRequestToGetByTitleEndpoint() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		searchBookByTitleForm.getInputByName("title").setValueAttribute(TITLE);
		searchBookByTitleForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.getBookByTitle(eq(new BookData(null, TITLE, null)), any(BindingResult.class), any(Model.class));
	}

	@Test
	public void testBookSearchByTitleView_whenUserFillTheFormWithInvalidTitleAndPressTheSubmitButton_shouldSendGetRequestToGetByTitleEndpoint() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		searchBookByTitleForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		searchBookByTitleForm.getButtonByName("submit-button").click();
		
		verify(bookWebController)
			.getBookByTitle(eq(new BookData(null, INVALID_TITLE, null)), any(BindingResult.class), any(Model.class));
	}

	@Test
	public void testBookSearchByTitleView_whenUserDoNotFillTheTitleAndPressTheSubmitButton_shouldNotSendGetRequestToGetByTitleEndpoint() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		searchBookByTitleForm.getButtonByName("submit-button").click();

		verify(bookWebController, never())
			.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class));
	}

	/* ---------- BookSearchByTitleView search-by-title form pre-filling capabilities tests ---------- */

	@Test
	public void testBookSearchByTitleView_afterGetRequestToGetByTitleEndpointWithValidTitle_theFormInputShouldBePrefilledWithProvidedTitle() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		
		assertThat(searchBookByTitleForm.getInputByName("title").getValueAttribute())
			.isEqualTo(TITLE);
	}

	@Test
	public void testBookSearchByTitleView_afterGetRequestToGetByTitleEndpointWithInvalidTitle_theFormInputShouldBePrefilledWithProvidedTitle() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + INVALID_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		
		assertThat(searchBookByTitleForm.getInputByName("title").getValueAttribute())
			.isEqualTo(INVALID_TITLE);
	}

	@Test
	public void testBookSearchByTitleView_whenJustOpened_theFormInputShouldNotBePrefilled() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		
		assertThat(searchBookByTitleForm.getInputByName("title").getValueAttribute())
			.isEmpty();
	}

	/* ---------- BookSearchByTitleView search-by-title form validation error messages tests ---------- */

	@Test
	public void testBookSearchByTitleView_afterGetRequestToGetByTitleEndpointWithNullTitle_shouldContainBlankValidationErrorMessage() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE +"?title=");
		
		assertThat(bookSearchByTitleView.getHtmlElementById("title-validation-error").asText())
			.isEqualTo("Please fill out this field");
	}

	@Test
	public void testBookSearchByTitleView_afterGetRequestToGetByTitleEndpointWithBlankTitle_shouldContainBlankValidationErrorMessage() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE);
		
		assertThat(bookSearchByTitleView.getHtmlElementById("title-validation-error").asText())
			.isEqualTo("Please fill out this field");
	}

	@Test
	public void testBookSearchByTitleView_afterGetRequestToGetByTitleEndpointWithInvalidTitle_shouldContainTitleValidationErrorMessage() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + INVALID_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		
		assertThat(searchBookByTitleForm.asText())
			.contains("Invalid title; the allowed special characters are: & , : . ! ?");
	}

	@Test
	public void testBookSearchByTitleView_whenUserFillTheFormWithInvalidTitleButDoNotPressTheSubmitButton_shouldNotShowValidationErrorMessage() throws Exception {
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_SEARCH_BY_TITLE);
		HtmlForm searchBookByTitleForm = bookSearchByTitleView.getFormByName("search-book-by-title-form");
		searchBookByTitleForm.getInputByName("title").setValueAttribute(INVALID_TITLE);
		
		assertThat(searchBookByTitleForm.asText())
			.doesNotContain("Invalid title; the allowed special characters are: & , : . ! ?");
	}

	/* ---------- BookSearchByTitleView book table tests ---------- */

	@Test
	public void testBookSearchByTitleView_untilBooksAreFound_shouldNotContainTheBookTable() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + INVALID_TITLE);
		
		assertThat(bookSearchByTitleView.getElementById("book-table-fragment"))
			.isNull();
	}

	@Test
	public void testBookSearchByTitleView_whenAnonymousUserAndBooksAreFound_shouldContainTheFoundedBooksInATableWithoutEditAndDeleteColumns() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, asList(
					new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
					new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST)));
				return VIEW_BOOK_SEARCH_BY_TITLE;
			}
		));
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);
		HtmlTable booksTable = bookSearchByTitleView.getHtmlElementById("book-table");
		
		assertThat(bookSearchByTitleView.getElementById("book-table-fragment"))
			.isNotNull();
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(booksTable.asText()))
			.isEqualTo(
				"ISBN-13"        + "	" + "Title"   + "	" + "Authors"      + "\n" +
				VALID_ISBN13     + "	" + TITLE     + "	" + AUTHORS_LIST   + "\n" +
				NEW_VALID_ISBN13 + "	" + NEW_TITLE + "	" + AUTHORS_LIST
		);
	}

	@Test
	@WithMockAdmin
	public void testBookSearchByTitleView_whenAdminAndBooksAreFound_shouldContainTheFoundedBooksInATableWithEditAndDeleteColumns() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, asList(
					new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
					new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST)));
				return VIEW_BOOK_SEARCH_BY_TITLE;
			}
		));
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);
		HtmlTable booksTable = bookSearchByTitleView.getHtmlElementById("book-table");
		
		assertThat(bookSearchByTitleView.getElementById("book-table-fragment"))
			.isNotNull();
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(booksTable.asText()))
			.isEqualTo(
				"ISBN-13"        + "	" + "Title"   + "	" + "Authors"      + "	" + "Edit book" + "	" + "Delete book" + "\n" +
				VALID_ISBN13     + "	" + TITLE     + "	" + AUTHORS_LIST   + "	" + " Edit "    + "	" + "Delete" + "\n" +
				NEW_VALID_ISBN13 + "	" + NEW_TITLE + "	" + AUTHORS_LIST   + "	" + " Edit "    + "	" + "Delete"
		);
	}

	@Test
	public void testBookSearchByTitleView_whenAnonymousUserAndBooksAreFound_shouldNotProvideAnEditLinkForEachBooksInTheTable() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, asList(
					new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
					new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST)));
				return VIEW_BOOK_SEARCH_BY_TITLE;
			}
		));
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);
		
		assertThatThrownBy(() -> bookSearchByTitleView.getAnchorByHref("/book/edit/" + VALID_ISBN13))
			.isInstanceOf(ElementNotFoundException.class);
		assertThatThrownBy(() -> bookSearchByTitleView.getAnchorByHref("/book/edit/" + NEW_VALID_ISBN13))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookSearchByTitleView_whenAdminAndBooksAreFound_shouldProvideAnEditLinkForEachBooksInTheTable() throws Exception {
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, asList(
					new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
					new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST)));
				return VIEW_BOOK_SEARCH_BY_TITLE;
			}
		));
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);
		bookSearchByTitleView.getAnchorByHref("/book/edit/" + VALID_ISBN13).click();
		bookSearchByTitleView.getAnchorByHref("/book/edit/" + NEW_VALID_ISBN13).click();
		
		InOrder inOrder = inOrder(bookWebController);
		inOrder.verify(bookWebController)
			.getBookEditView(
				eq(new IsbnData(VALID_ISBN13_WITHOUT_FORMATTING)),
				any(BindingResult.class),
				eq(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, null, null)));
		inOrder.verify(bookWebController)
			.getBookEditView(
				eq(new IsbnData(NEW_VALID_ISBN13_WITHOUT_FORMATTING)),
				any(BindingResult.class),
				eq(new BookData(NEW_VALID_ISBN13_WITHOUT_FORMATTING, null, null)));
	}

	@Test
	public void testBookSearchByTitleView_whenAnonymousUserAndBooksAreFound_shouldNotProvideADeleteDialogForEachBooksInTheTable() throws Exception {
		List<Book> bookList = asList(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
			new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_SEARCH_BY_TITLE;
			}
		));
		
		for (Book book : bookList) {
			
			String isbn = Long.toString(book.getIsbn());
			List<String> dialogContent = asList(
				"Do you really want to delete this book?",
				isbn + " - " + book.getTitle() + " - " + book.getAuthors(),
				"No", "Yes, delete"
			);
			
			HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);

			assertThat(bookSearchByTitleView.getElementById("getDeleteBookDialogButton-" + isbn))
				.isNull();
			assertThat(bookSearchByTitleView.getElementById("deleteBookDialog-" + isbn))
				.isNull();
			assertThat(bookSearchByTitleView.asText())
				.doesNotContain(dialogContent);
		}
	}

	@Test
	@WithMockAdmin
	public void testBookSearchByTitleView_whenAdminAndBooksAreFound_shouldProvideADeleteDialogForEachBooksInTheTable() throws Exception {
		List<Book> bookList = asList(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
			new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
		when(bookWebController.getBookByTitle(any(BookData.class), any(BindingResult.class), any(Model.class)))
			.thenAnswer(answer((BookData form, BindingResult result, Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_SEARCH_BY_TITLE;
			}
		));
		when(bookWebController.postDeleteBook(any(IsbnData.class), any(BindingResult.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		for (Book book : bookList) {
			
			String isbn = Long.toString(book.getIsbn());
			List<String> dialogContent = asList(
				"Do you really want to delete this book?",
				isbn + " - " + book.getTitle() + " - " + book.getAuthors(),
				"No", "Yes, delete"
			);
			
			HtmlPage bookSearchByTitleView = webClient.getPage(URI_BOOK_GET_BY_TITLE + "?title=" + TITLE);
			assertThat(bookSearchByTitleView.asText())
				.doesNotContain(dialogContent);

			bookSearchByTitleView.getHtmlElementById("getDeleteBookDialogButton-" + isbn).click();
			webClient.waitForBackgroundJavaScript(1000);
			assertThat(bookSearchByTitleView.asText())
				.contains(dialogContent);
			
			bookSearchByTitleView.getHtmlElementById("deleteBookDialog-" + isbn + "-closeButton").click();
			webClient.waitForBackgroundJavaScript(1000);
			assertThat(bookSearchByTitleView.asText())
				.doesNotContain(dialogContent);
			
			bookSearchByTitleView.getHtmlElementById("getDeleteBookDialogButton-" + isbn).click();
			webClient.waitForBackgroundJavaScript(1000);
			bookSearchByTitleView.getHtmlElementById("deleteBookDialog-" + isbn + "-noButton").click();
			webClient.waitForBackgroundJavaScript(1000);
			assertThat(bookSearchByTitleView.asText())
				.doesNotContain(dialogContent);
			
			bookSearchByTitleView.getHtmlElementById("getDeleteBookDialogButton-" + isbn).click();
			webClient.waitForBackgroundJavaScript(1000);
			bookSearchByTitleView.getHtmlElementById("deleteBookDialog-" + isbn + "-yesButton").click();
			verify(bookWebController)
				.postDeleteBook(eq(new IsbnData(isbn)), any(BindingResult.class));
		}
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
