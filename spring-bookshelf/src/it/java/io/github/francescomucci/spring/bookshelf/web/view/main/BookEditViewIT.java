package io.github.francescomucci.spring.bookshelf.web.view.main;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.view.page.helper.AuthenticationHelperMethods.*;
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
import io.github.francescomucci.spring.bookshelf.web.view.page.helper.SilentHtmlUnitDriver;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookEditPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookEditViewIT {

	@Autowired
	private BookRepository bookRepository;

	private String bookEditUrl;

	private WebDriver webDriver;

	@LocalServerPort
	private int portNumber;

	@Before
	public void setup() {
		bookRepository.deleteAll();
		bookEditUrl = "http://localhost:" + portNumber + "/book/edit/";
		webDriver = new SilentHtmlUnitDriver();
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

	/* ---------- BookEditView functionality tests ---------- */

	@Test
	public void testBookEditView_editBookForm_withValidFormData_canUpdateEditedBookIntoRepository() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.fillEditFormAndPressSubmitButton(NEW_TITLE, AUTHORS_STRING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
		assertThat(bookRepository.findById(toEditBook.getIsbn()).get().getTitle())
			.isEqualTo(NEW_TITLE);
	}

	@Test
	public void testBookEditView_editBookForm_withInvalidFormData_cannotUpdateEditedBookIntoRepository() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.fillEditFormAndPressSubmitButton(INVALID_TITLE, INVALID_AUTHORS_STRING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book edit view");
		assertThat(bookRepository.findById(toEditBook.getIsbn()).get().getTitle())
			.isEqualTo(TITLE);
	}

	@Test
	public void testBookEditView_saveEditedBook_withValidButUnusedIbsn_canOpenBookNotFoundView() {
		/* To send post save with valid but unused isbn,
		 * we directly delete the book from database after getting the corresponding book edit view. */
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		bookRepository.deleteAll();
		MyPage returnedPage = bookEditPage.fillEditFormAndPressSubmitButton(NEW_TITLE, AUTHORS_STRING);
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book not found error view");
		assertThat(bookRepository.findById(toEditBook.getIsbn()))
			.isEmpty();
	}

	/* ---------- BookEditView navigation bar tests ---------- */

	@Test
	public void testBookEditView_homePageLink_canOpenBookHomePage() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.clickNavbarHomeLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book home view");
	}

	@Test
	public void testBookEditView_showBookListLink_canOpenBookListPage() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.clickNavbarShowBookListLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book list view");
	}

	@Test
	public void testBookEditView_searchBookByIsbnLink_canOpenBookSearchByIsbnPage() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.clickNavbarSearchBookByIsbnLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by ISBN view");
	}

	@Test
	public void testBookEditView_searchBookByTitleLink_canOpenBookSearchByTitlePage() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.clickNavbarSearchBooksByTitleLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book search by title view");
	}

	@Test
	public void testBookEditView_addNewBookLink_canOpenBookNewPage() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentials(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		MyPage returnedPage = bookEditPage.clickNavbarAddNewBookLink();
		
		assertThat(returnedPage.getPageTitle())
			.isEqualTo("Book new view");
	}

	@Test
	public void testBookEditView_logoutLink_canLogoutSuccesfully() {
		Book toEditBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		loginWithValidCredentialsAndRememberMe(webDriver, portNumber);
		
		webDriver.get(bookEditUrl + toEditBook.getIsbn());
		BookEditPage bookEditPage = new BookEditPage(webDriver);
		BookHomePage bookHomePage = (BookHomePage) bookEditPage.clickLogoutButton();
		
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
		assertThat(webDriver.manage().getCookieNamed("spring-bookshelf-remember-me"))
			.isNull();
	}

}
