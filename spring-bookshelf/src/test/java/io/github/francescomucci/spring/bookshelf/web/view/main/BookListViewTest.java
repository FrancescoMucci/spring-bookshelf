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
public class BookListViewTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private BookWebController bookWebController;

	@Before
	public void setup() {
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getCookieManager().clearCookies();
	}

	/* ---------- BookListView header message tests ---------- */

	@Test
	public void testBookListView_whenDbIsEmpty_shouldContainAWarningMessageInTheHeader() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlHeader header = (HtmlHeader) bookListView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Empty database" + "\n" +
				MESSAGE_EMPTY_DB);
	}

	@Test
	public void testBookListView_whenDbIsNotEmpty_shouldContainAnInformativeTextInTheHeader() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlHeader header = (HtmlHeader) bookListView.getElementsByTagName("header").get(0);
		
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(header.asText()))
			.isEqualTo(
				"Book list table" + "\n" +
				"This table contains all the book in the database");
	}

	/* ---------- BookListView empty DB advice tests ---------- */

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdminAndDbIsEmpty_shouldContainSomeAdvice() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		
		assertThat(bookListView.asText())
			.contains(
				"Advice",
				"Try to add some book to the database",
				"Use the link Add new book in the navigation bar"
		);
	}

	@Test
	public void testBookListView_whenAnonymousUserAndDbIsEmpty_shouldNotContainAnyAdvice() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		
		assertThat(bookListView.asText())
			.doesNotContain(
				"Advice",
				"Try to add some book to the database",
				"Use the link Add new book in the navigation bar"
		);
	}

	/* ---------- BookListView book table layout and text content tests ---------- */

	@Test
	public void testBookListView_whenAnonymousUserAndDbIsNotEmpty_shouldShowBooksInATableWithoutEditAndDeleteColumns() throws Exception {
		List<Book> bookList = asList(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlTable booksTable = bookListView.getHtmlElementById("book-table");
		
		assertThat(bookListView.getElementById("book-table-fragment"))
			.isNotNull();
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(booksTable.asText()))
			.isEqualTo(
				"ISBN-13"      + "	" + "Title" + "	" + "Authors"      + "\n" +
				VALID_ISBN13   + "	" + TITLE   + "	" + AUTHORS_LIST   + "\n" +
				VALID_ISBN13_2 + "	" + TITLE_2 + "	" + AUTHORS_LIST_2
		);
	}

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdminAndDbIsNotEmpty_shouldShowBooksInATableWithEditAndDeleteColumns() throws Exception {
		List<Book> bookList = asList(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlTable booksTable = bookListView.getHtmlElementById("book-table");
		
		assertThat(bookListView.getElementById("book-table-fragment"))
			.isNotNull();
		assertThat(BookViewTestingHelperMethods.removeWindowsCR(booksTable.asText()))
			.isEqualTo(
				"ISBN-13"      + "	" + "Title" + "	" + "Authors"      + "	" + "Edit book" + "	" + "Delete book" + "\n" +
				VALID_ISBN13   + "	" + TITLE   + "	" + AUTHORS_LIST   + "	" + " Edit "      + "	" + "Delete"      + "\n" +
				VALID_ISBN13_2 + "	" + TITLE_2 + "	" + AUTHORS_LIST_2 + "	" + " Edit "      + "	" + "Delete"
		);
	}

	@Test
	public void testBookListView_whenDbIsEmpty_shouldNotContainTheBookTable() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		
		assertThat(bookListView.getElementById("book-table-fragment"))
			.isNull();
		assertThat(bookListView.getElementsByTagName("table"))
			.isEmpty();
	}

	/* ---------- BookListView book table edit link tests ---------- */

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdminAndDbIsNotEmpty_shouldProvideAnEditLinkForEachBookInTheTable() throws Exception {
		List<Book> bookList = asList(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		when(bookWebController.getBookEditView(any(IsbnData.class), any(BindingResult.class), any(BookData.class)))
			.thenReturn(VIEW_BOOK_EDIT);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		bookListView.getAnchorByHref("/book/edit/" + VALID_ISBN13).click();
		bookListView.getAnchorByHref("/book/edit/" + VALID_ISBN13_2).click();
		
		InOrder inOrder = inOrder(bookWebController);
		inOrder.verify(bookWebController)
			.getBookEditView(
				eq(new IsbnData(VALID_ISBN13_WITHOUT_FORMATTING)),
				any(BindingResult.class),
				eq(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, null, null)));
		inOrder.verify(bookWebController)
			.getBookEditView(
				eq(new IsbnData(VALID_ISBN13_2_WITHOUT_FORMATTING)),
				any(BindingResult.class),
				eq(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, null, null)));
	}

	@Test
	public void testBookListView_whenAnonymousUserAndDbIsNotEmpty_shouldNotProvideAnEditLinkForEachBookInTheTable() throws Exception {
		List<Book> bookList = asList(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		
		assertThatThrownBy(() -> bookListView.getAnchorByHref("/book/edit/" + VALID_ISBN13))
			.isInstanceOf(ElementNotFoundException.class);
		assertThatThrownBy(() -> bookListView.getAnchorByHref("/book/edit/" + VALID_ISBN13_2))
			.isInstanceOf(ElementNotFoundException.class);
	}

	/* ---------- BookListView book table get delete dialog button tests---------- */

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdminAndDbIsNotEmpty_shouldProvideAButtonToOpenTheDeleteDialogForEachBookInTheTable() throws Exception {
		List<Book> bookList = asList(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), 
			new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2)
		);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		
		for (Book book : bookList) {
			
			String isbn = Long.toString(book.getIsbn());
			List<String> deleteDialogContent = asList(
				"Do you really want to delete this book?",
				isbn + " - " + book.getTitle() + " - " + book.getAuthors(),
				"No", 
				"Yes, delete");
			
			HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
			
			bookListView.getElementById("getDeleteBookDialogButton-" + isbn).click();
			webClient.waitForBackgroundJavaScript(1000);
			
			assertThat(bookListView.asText())
				.contains(deleteDialogContent);
			
		}
	}

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdminAndDbIsNotEmptyButDeleteButtonNotPressed_shouldNotShowTheDeleteDialog() throws Exception {
		List<Book> bookList = asList(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), 
			new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2)
		);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		
		for (Book book : bookList) {
			
			List<String> deleteDialogContent = asList(
				"Do you really want to delete this book?",
				book.getIsbn() + " - " + book.getTitle() + " - " + book.getAuthors(),
				"No", 
				"Yes, delete");
			
			HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
			
			assertThat(bookListView.asText())
				.doesNotContain(deleteDialogContent);
		}
	}

	@Test
	public void testBookListView_whenAnonymousUserAndDbIsNotEmpty_shouldNotProvideAButtonToOpenTheDeleteDialogForEachBookInTheTable() throws Exception {
		List<Book> bookList = asList(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), 
			new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2)
		);
		when(bookWebController.getBookListView(any(Model.class)))
			.thenAnswer(answer((Model model)-> {
				model.addAttribute(MODEL_BOOKS, bookList);
				return VIEW_BOOK_LIST;
			}
		));
		
		for (Book book : bookList) {
			
			String isbn = Long.toString(book.getIsbn());
			List<String> deleteDialogContent = asList(
				"Do you really want to delete this book?",
				isbn + " - " + book.getTitle() + " - " + book.getAuthors(),
				"No", 
				"Yes, delete");
			
			HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
			
			assertThat(bookListView.getElementById("getDeleteBookDialogButton-" + isbn))
				.isNull();
			assertThat(bookListView.getElementById("deleteBookDialog-" + isbn))
				.isNull();
			assertThat(bookListView.asText())
				.doesNotContain(deleteDialogContent);
			
		}
	}

	/* ---------- BookListView layout tests ---------- */

	@Test
	public void testBookListView_shouldAlwaysHaveATitle() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
	
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
	
		assertThat(bookListView.getTitleText())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookListView_shouldAlwaysProvideALinkToHomePageInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor homePageLink = bookListView.getAnchorByText("Mucci's bookshelf");
		homePageLink.click();
		
		assertThat(homePageLink.getAncestors().toString())
			.contains("nav");
		assertThat(homePageLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_HOME);
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookListView_shouldAlwaysProvideALinkToViewAllBooksInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor showBookListLink = bookListView.getAnchorByText("Show book list");
		showBookListLink.click();
		
		assertThat(showBookListLink.getAncestors().toString())
			.contains("nav");
		assertThat(showBookListLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_LIST);
		verify(bookWebController, times(2))
			.getBookListView(any(Model.class));
	}

	@Test
	public void testBookListView_shouldAlwaysProvideALinkToSearchBookByIsbnInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		when(bookWebController.getBookSearchByIsbnView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_ISBN);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor searchBookByIsbnLink = bookListView.getAnchorByText("Search book by isbn");
		searchBookByIsbnLink.click();
		
		assertThat(searchBookByIsbnLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByIsbnLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_ISBN);
		verify(bookWebController)
			.getBookSearchByIsbnView(new BookData());
	}

	@Test
	public void testBookListView_shouldAlwaysProvideALinkToSearchBooksByTitleInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		when(bookWebController.getBookSearchByTitleView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_SEARCH_BY_TITLE);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor searchBookByTitleLink = bookListView.getAnchorByText("Search book by title");
		searchBookByTitleLink.click();
		
		assertThat(searchBookByTitleLink.getAncestors().toString())
			.contains("nav");
		assertThat(searchBookByTitleLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_SEARCH_BY_TITLE);
		verify(bookWebController)
			.getBookSearchByTitleView(new BookData());
	}

	@Test
	public void testBookListView_whenAnonymousUser_shouldNotProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		
		assertThatThrownBy(() -> bookListView.getAnchorByText("Add new book"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdmin_shouldProvideALinkToAddNewBookInTheNavbar() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		when(bookWebController.getBookNewView(any(BookData.class)))
			.thenReturn(VIEW_BOOK_NEW);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlAnchor addNewBookLink = bookListView.getAnchorByText("Add new book");
		addNewBookLink.click();
		
		assertThat(addNewBookLink.getAncestors().toString())
			.contains("nav");
		assertThat(addNewBookLink.getHrefAttribute())
			.isEqualTo(URI_BOOK_NEW);
		verify(bookWebController)
			.getBookNewView(new BookData());
	}

	@Test
	public void testBookListView_whenAnonymousUser_shouldNotProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		
		assertThatThrownBy(() -> bookListView.getAnchorByText("Logout"))
			.isInstanceOf(ElementNotFoundException.class);
	}

	@Test
	@WithMockAdmin
	public void testBookListView_whenAdmin_shouldProvideALinkToLogout() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		when(bookWebController.getBookHomeView())
			.thenReturn(VIEW_BOOK_HOME);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlForm logoutForm = bookListView.getFormByName("logout-form");
		logoutForm.getButtonByName("logout-button").click();
		
		assertThat(logoutForm.getActionAttribute())
			.isEqualTo("/logout");
		verify(bookWebController)
			.getBookHomeView();
	}

	@Test
	public void testBookListView_shouldAlwaysContainTheCopyrightInTheFooter() throws Exception {
		when(bookWebController.getBookListView(any(Model.class)))
			.thenReturn(VIEW_BOOK_LIST);
		
		HtmlPage bookListView = webClient.getPage(URI_BOOK_LIST);
		HtmlFooter footer = (HtmlFooter) bookListView.getElementsByTagName("footer").get(0);
		
		assertThat(footer.asText())
			.contains("© 2021 Copyright: Francesco Mucci");
	}

}
