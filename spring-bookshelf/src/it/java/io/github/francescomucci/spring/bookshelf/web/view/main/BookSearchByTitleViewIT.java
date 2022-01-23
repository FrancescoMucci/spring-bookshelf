package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.view.helper.AuthenticationHelperMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;
import io.github.francescomucci.spring.bookshelf.web.view.helper.SilentHtmlUnitDriver;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByTitlePage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookSearchByTitleViewIT {

	@Autowired
	private BookRepository bookRepository;

	private String bookSearchByTitleUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		bookRepository.deleteAll();
		bookSearchByTitleUrl = "http://localhost:" + portNumber + URI_BOOK_SEARCH_BY_TITLE;
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookSearchByTitleView functionality tests ---------- */

	@Test
	public void testBookSearchByTitleView_searchForm_whenSearchedTitleIsNotFound_shouldOpenBookNotFoundPage() {
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		bookRepository.save(new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.fillSearchFormAndPressSubmitButton(UNUSED_TITLE);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book not found error view");
	}

	@Test
	public void testBookSearchByTitleView_searchForm_whenSearchedTitleIsFound_shouldShowRetrievedBooks() {
		Book otherBook = bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		Book toBeRetrievedBook2 = bookRepository.save(new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
		Book toBeRetrievedBook1 = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.fillSearchFormAndPressSubmitButton("" + toBeRetrievedBook1.getTitle());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
		assertThat(bookSearchByTitlePage.getInputValue())
			.isEqualTo(toBeRetrievedBook1.getTitle());
		assertThat(bookSearchByTitlePage.getBookTable())
			.contains(
				"" + toBeRetrievedBook1.getIsbn(), toBeRetrievedBook1.getTitle(), "" + toBeRetrievedBook1.getAuthors(),
				"" + toBeRetrievedBook2.getIsbn(), toBeRetrievedBook2.getTitle(), "" + toBeRetrievedBook2.getAuthors())
			.doesNotContain("" + otherBook.getIsbn(), otherBook.getTitle(), "" + otherBook.getAuthors());
	}


	@Test
	public void testBookSearchByTitleView_editLinkForFoundedBook_canOpenBookEditPage() {
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		bookSearchByTitlePage.fillSearchFormAndPressSubmitButton("" + book.getTitle());
		MyPage returnedPage = bookSearchByTitlePage.clickEditLink(book.getIsbn());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book edit view");
	}

	@Test
	public void testBookSearchByTitleView_deleteDialogForFoundedBook_canRemoveBookFromRepository() {
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		bookSearchByTitlePage.fillSearchFormAndPressSubmitButton("" + book.getTitle());
		MyPage returnedPage = bookSearchByTitlePage.clickShowDeleteDialogAndThenYesDeleteButton(book.getIsbn());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
		assertThat(bookRepository.findById(book.getIsbn()))
			.isEmpty();
	}

	@Test
	public void testBookSearchByTitleView_deleteBook_whitValidIsbnButUnused_canOpenBookNotFoundView() {
		/* To send post delete with valid but unused isbn,
		 * we directly delete the document from database after getting the book list view. */
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		bookSearchByTitlePage.fillSearchFormAndPressSubmitButton("" + book.getTitle());
		bookRepository.deleteAll();
		MyPage returnedPage = bookSearchByTitlePage.clickShowDeleteDialogAndThenYesDeleteButton(book.getIsbn());

		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book not found error view");
	}

	/* ---------- BookSearchByTitleView navigation bar tests ---------- */

	@Test
	public void testBookSearchByTitleView_homePageLink_canOpenBookHomePage() {
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookSearchByTitleView_showBookListLink_canOpenBookListPage() {
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookSearchByTitleView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookSearchByTitleView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookSearchByTitleView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		MyPage returnedPage = bookSearchByTitlePage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookSearchByIsbnView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(bookSearchByTitleUrl);
		BookSearchByTitlePage bookSearchByTitlePage = new BookSearchByTitlePage(webDriver);
		BookHomePage bookHomePage = (BookHomePage) bookSearchByTitlePage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
