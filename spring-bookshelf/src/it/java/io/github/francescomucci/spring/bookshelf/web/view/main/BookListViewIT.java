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
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookListPage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookListViewIT {

	@Autowired
	private BookRepository bookRepository;

	private String bookListUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		bookRepository.deleteAll();
		bookListUrl = "http://localhost:" + portNumber + URI_BOOK_LIST;
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookListView functionality tests ---------- */

	@Test
	public void testBookListView_table_canShowRetrievedBooks() {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		String tableContent = bookListPage.getBookTable();
		
		assertThat(tableContent).contains(
			VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING,
			VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	@Test
	public void testBookListView_editLink_canOpenBookEditPage() {
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickEditLink(book.getIsbn());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book edit view");
	}

	@Test
	public void testBookListView_deleteDialog_canRemoveBookFromRepository() {
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickShowDeleteDialogAndThenYesDeleteButton(book.getIsbn());
	
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
		assertThat(bookRepository.findById(book.getIsbn()))
			.isEmpty();
	}

	@Test
	public void testBookListView_deleteBook_whitValidIsbnButUnused_canOpenBookNotFoundView() {
		/* To send post delete with valid but unused isbn,
		 * we directly delete the document from database after getting the book list view. */
		Book book = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		bookRepository.deleteAll();
		MyPage returnedPage = bookListPage.clickShowDeleteDialogAndThenYesDeleteButton(book.getIsbn());
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book not found error view");
	}

	/* ---------- BookListView navigation bar tests ---------- */

	@Test
	public void testBookListView_homePageLink_canOpenBookHomePage() {
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookListView_showBookListLink_canOpenBookListPage() {
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookListView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookListView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookListView_addNewBookLink_canOpenBookNewPage() {
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		MyPage returnedPage = bookListPage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookListView_logoutLink_canLogoutSuccesfully() {
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(bookListUrl);
		BookListPage bookListPage = new BookListPage(webDriver);
		BookHomePage bookHomePage = bookListPage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
