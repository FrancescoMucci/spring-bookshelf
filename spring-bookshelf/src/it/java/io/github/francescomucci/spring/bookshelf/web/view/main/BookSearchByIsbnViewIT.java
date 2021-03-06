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
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByIsbnPage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookSearchByIsbnViewIT {

	@Autowired
	private BookRepository bookRepository;

	private String bookSearchByIsbnUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		bookRepository.deleteAll();
		bookSearchByIsbnUrl = "http://localhost:" + portNumber + URI_BOOK_SEARCH_BY_ISBN;
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookSearchByIsbnView functionality tests ---------- */

	@Test
	public void testBookSearchByTitleView_searchForm_whenSearchedIsbnIsNotFound_shouldOpenBookNotFoundPage() {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.fillSearchFormAndSubmitExpectingError(UNUSED_ISBN13_WITH_HYPHENS);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book not found error view");
	}

	@Test
	public void testBookSearchByIsbnView_searchForm_whenSearchedIsbnIsFound_shouldShowRetrievedBook() {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.fillSearchFormAndSubmit(VALID_ISBN13_WITHOUT_FORMATTING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
		assertThat(bookSearchByIsbnPage.getInputValue())
			.isEqualTo(VALID_ISBN13_WITHOUT_FORMATTING);
		assertThat(bookSearchByIsbnPage.getBookTable())
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.doesNotContain(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	@Test
	public void testBookSearchByIsbnView_editLinkForFoundedBook_canOpenBookEditPage() {
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		bookSearchByIsbnPage.fillSearchFormAndSubmit("" + book.getIsbn());
		MyPage returnedPage = bookSearchByIsbnPage.clickEditLink(book.getIsbn());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book edit view");
	}

	@Test
	public void testBookSearchByIsbnView_deleteDialogForFoundedBook_canRemoveBookFromRepository() {
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		bookSearchByIsbnPage.fillSearchFormAndSubmit("" + book.getIsbn());
		MyPage returnedPage = bookSearchByIsbnPage.clickDeleteAndThenYes(book.getIsbn());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
		assertThat(bookRepository.findById(book.getIsbn()))
			.isEmpty();
	}

	@Test
	public void testBookSearchByIsbnView_deleteBook_whitValidIsbnButUnused_canOpenBookNotFoundView() {
		/* To send post delete with valid but unused isbn,
		 * we directly delete the document from database after getting the book list view. */
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		bookSearchByIsbnPage.fillSearchFormAndSubmit("" + book.getIsbn());
		bookRepository.deleteAll();
		MyPage returnedPage = bookSearchByIsbnPage.clickDeleteAndThenYesExpectingError(book.getIsbn());

		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book not found error view");
	}

	/* ---------- BookSearchByIsbnView navigation bar tests ---------- */

	@Test
	public void testBookSearchByIsbnView_homePageLink_canOpenBookHomePage() {
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookSearchByIsbnView_showBookListLink_canOpenBookListPage() {
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookSearchByIsbnView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookSearchByIsbnView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookSearchByIsbnView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		MyPage returnedPage = bookSearchByIsbnPage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookSearchByIsbnView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(bookSearchByIsbnUrl);
		BookSearchByIsbnPage bookSearchByIsbnPage = new BookSearchByIsbnPage(webDriver);
		BookHomePage bookHomePage = bookSearchByIsbnPage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
