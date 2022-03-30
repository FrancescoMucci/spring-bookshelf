package io.github.francescomucci.spring.bookshelf;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookListPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookEditPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookNewPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByIsbnPage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookSearchByTitlePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.error.MyErrorPage;

/* Before run this test, make sure your SpringBoot application and MongoDB are up and running */

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class SpringBookshelfApplicationE2E {

	private static final int PORT_NUMBER = Integer.parseInt(System.getProperty("server.port", "8080"));
	private static final String BASE_URL = "http://localhost:" + PORT_NUMBER;
	private static final String DB_COLLECTION = "Book";

	@Value("${spring.data.mongodb.port:27017}")
	private int db_port_number;

	@Value("${spring.data.mongodb.database:test}")
	private String db_name;

	private MongoClient mongoClient;
	private WebDriver webDriver;
	private ChromeOptions options;
	private BookHomePage bookHomePage;

	@BeforeClass
	public static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}

	@Before
	public void setup() {
		mongoClient = MongoClients.create("mongodb://localhost:" + db_port_number);
		mongoClient.getDatabase(db_name).drop();
		setupAddingBookToDatabase(VALID_ISBN13, TITLE, AUTHORS_LIST);
		setupAddingBookToDatabase(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		
		options = new ChromeOptions();
		options.addArguments("start-maximized", "user-data-dir=target/web-driver-data/chrome-user-data");
		webDriver = new ChromeDriver(options);
		webDriver.get(BASE_URL);
		bookHomePage = new BookHomePage(webDriver);
	}

	@After
	public void teardown() {
		webDriver.manage().deleteCookieNamed(REMEMBER_ME_TOKEN);
		webDriver.quit();
		
		mongoClient.close();
	}

	/* ---------- SpringBookshelfApplication login tests ---------- */

	@Test
	public void testSpringBookshelfApplication_login_whenInvalidCredentials_shouldNotSuccessfullyLogin() {
		bookHomePage.fillLoginFormAndPressSubmitButton(INVALID_USER_NAME, INVALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome to my book library");
		assertThat(bookHomePage.getAuthenticationErrorMessage())
			.isEqualTo("Invalid user name or password");
	}

	@Test
	public void testSpringBookshelfApplication_login_whenValidCredentials_shouldSuccessfullyLogin() {
		bookHomePage.fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome back");
	}

	@Test
	public void testSpringBookshelfApplication_login_whenValidCredentialsButNoRememberMe_shouldNotRememberPrevioslyLoggedUser() {
		bookHomePage.fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome back");
		
		webDriver.quit();
		webDriver = new ChromeDriver(options);
		webDriver.get(BASE_URL);
		bookHomePage = new BookHomePage(webDriver);
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome to my book library");
	}

	@Test
	public void testSpringBookshelfApplication_login_whenValidCredentialsAndRememberMe_shouldRememberPrevioslyLoggedUser() {
		bookHomePage.checkRemeberMe();
		bookHomePage.fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome back");
		
		webDriver.quit();
		webDriver = new ChromeDriver(options);
		webDriver.get(BASE_URL);
		bookHomePage = new BookHomePage(webDriver);
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome back");
	}

	/* ---------- SpringBookshelfApplication logout tests ---------- */

	@Test
	public void testSpringBookshelfApplication_logout_shouldSuccessfullyLogout() {
		bookHomePage.loginWithValidCredentials();
		
		bookHomePage.clickLogoutButton();
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome to my book library");
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
	}

	@Test
	public void testSpringBookshelfApplication_logout_whenLoggedWithRememberMe_shouldSuccessfullyLogout() {
		bookHomePage.checkRemeberMe();
		bookHomePage.loginWithValidCredentials();
		
		bookHomePage.clickLogoutButton();
		
		assertThat(bookHomePage.getHeaderContent())
			.contains("Welcome to my book library");
		assertThat(bookHomePage.getLogoutMessage())
			.isEqualTo("Logged out successfully");
	}

	/* ---------- SpringBookshelfApplication showBookList tests ---------- */

	@Test
	public void testSpringBookshelfApplication_showBookList_whenNoBookAdded_shouldShowEmptyMessage() {
		mongoClient.getDatabase(db_name).drop();
		
		BookListPage bookListPage = bookHomePage.clickNavbarShowBookListLink();
		
		assertThat(bookListPage.getHeaderContent())
			.contains("Empty database");
	}

	@Test
	public void testSpringBookshelfApplication_showBookList_whenSomeBookAdded_shouldShowBooksInATables() {
		BookListPage bookListPage = bookHomePage.clickNavbarShowBookListLink();
		
		assertThat(bookListPage.getBookTable())
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.contains(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	/* ---------- SpringBookshelfApplication addNewBook tests ---------- */

	@Test
	public void testSpringBookshelfApplication_addNewBook_withInvalidData_shouldShowValidationErrorMessages() {
		bookHomePage.loginWithValidCredentials();
		BookNewPage bookNewPage = bookHomePage.clickNavbarAddNewBookLink();
		bookNewPage.fillAddFormAndPressSubmitButton(INVALID_ISBN13_WITH_SPACES, INVALID_TITLE, INVALID_AUTHORS_STRING);
		
		assertThat(bookNewPage.getIsbnValidationErrorMessage())
			.contains("Invalid ISBN-13");
		assertThat(bookNewPage.getTitleValidationErrorMessage())
			.contains("Invalid title");
		assertThat(bookNewPage.getAuthorsValidationErrorMessage())
			.contains("Invalid authors");
	}

	@Test
	public void testSpringBookshelfApplication_addNewBook_withValidDataButAlreadyUsedIsbn_shouldOpenBookAlreadyExistErrorView() {
		bookHomePage.loginWithValidCredentials();
		BookNewPage bookNewPage = bookHomePage.clickNavbarAddNewBookLink();
		MyErrorPage errorPage = (MyErrorPage) 
			bookNewPage.fillAddFormAndPressSubmitButton(VALID_ISBN13_WITH_SPACES, TITLE, AUTHORS_STRING);
		
		assertThat(errorPage.getPageTitle())
			.isEqualTo("Book already exist error view");
		assertThat(errorPage.getErrorMessage())
			.isEqualTo(VALID_ISBN13 + ": a book with this ISBN-13 already exist");
	}

	@Test
	public void testSpringBookshelfApplication_addNewBook_withValidDataAndUnusedIsbn_shouldOpenBookListViewToShowNewlyAddedBook() {
		bookHomePage.loginWithValidCredentials();
		BookNewPage bookNewPage = bookHomePage.clickNavbarAddNewBookLink();
		BookListPage bookListPage = (BookListPage) 
			bookNewPage.fillAddFormAndPressSubmitButton(UNUSED_ISBN13_WITHOUT_FORMATTING, UNUSED_TITLE, UNUSED_AUTHORS_STRING);
		
		assertThat(bookListPage.getBookTable())
			.contains(UNUSED_ISBN13_WITHOUT_FORMATTING, UNUSED_TITLE, UNUSED_AUTHORS_STRING);
	}

	/* ---------- SpringBookshelfApplication editBook tests ---------- */

	@Test
	public void testSpringBookshelfApplication_editBook_withInvalidFormData_shouldShowValidationErrorMessages() {
		bookHomePage.loginWithValidCredentials();
		BookListPage bookListPage = bookHomePage.clickNavbarShowBookListLink();
		BookEditPage bookEditPage = (BookEditPage) bookListPage.clickEditLink(VALID_ISBN13);
		bookEditPage.fillEditFormAndPressSubmitButton(INVALID_TITLE, INVALID_AUTHORS_STRING);
		
		assertThat(bookEditPage.getTitleValidationErrorMessage())
			.contains("Invalid title");
		assertThat(bookEditPage.getAuthorsValidationErrorMessage())
			.contains("Invalid authors");
	}

	@Test
	public void testSpringBookshelfApplication_editBook_withValidFormData_shouldOpenBookListViewToShowUpdatedBook() {
		bookHomePage.loginWithValidCredentials();
		BookListPage bookListPage = bookHomePage.clickNavbarShowBookListLink();
		BookEditPage bookEditPage = (BookEditPage) bookListPage.clickEditLink(VALID_ISBN13);
		bookListPage = (BookListPage) 
			bookEditPage.fillEditFormAndPressSubmitButton(NEW_TITLE, AUTHORS_STRING);
		
		assertThat(bookListPage.getBookTable())
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, NEW_TITLE, AUTHORS_STRING);
	}

	/* ---------- SpringBookshelfApplication searchBookByIsbn tests ---------- */

	@Test
	public void testSpringBookshelfApplication_searchBookByIsbn_withInvalidIsbn_shouldShowValidationErrorMessage() {
		BookSearchByIsbnPage searchByIsbnPage = bookHomePage.clickNavbarSearchBookByIsbnLink();
		searchByIsbnPage.fillSearchFormAndPressSubmitButton(INVALID_ISBN13_WITH_HYPHENS);
		
		assertThat(searchByIsbnPage.getValidationErrorMessage())
			.contains("Invalid ISBN-13");
	}

	@Test
	public void testSpringBookshelfApplication_searchBookByIsbn_withValidButUnusedIsbn_shouldOpenBookNotFoundErrorView() {
		BookSearchByIsbnPage searchByIsbnPage = bookHomePage.clickNavbarSearchBookByIsbnLink();
		MyErrorPage errorPage = (MyErrorPage) 
			searchByIsbnPage.fillSearchFormAndPressSubmitButton(UNUSED_ISBN13_WITH_HYPHENS);
		
		assertThat(errorPage.getPageTitle())
			.isEqualTo("Book not found error view");
		assertThat(errorPage.getErrorMessage())
			.isEqualTo(UNUSED_ISBN13 + ": no book found with this ISBN-13");
	}

	@Test
	public void testSpringBookshelfApplication_searchBookByIsbn_withValidAndUsedIsbn_shouldShowRetrievedBookInATable() {
		BookSearchByIsbnPage searchByIsbnPage = bookHomePage.clickNavbarSearchBookByIsbnLink();
		searchByIsbnPage.fillSearchFormAndPressSubmitButton(VALID_ISBN13_WITH_HYPHENS);
		
		assertThat(searchByIsbnPage.getBookTable())
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.doesNotContain(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	/* ---------- SpringBookshelfApplication searchBookByTitle tests ---------- */

	@Test
	public void testSpringBookshelfApplication_searchBookByTitle_withInvalidTitle_shouldShowValidationErrorMessage() {
		BookSearchByTitlePage searchByTitlePage = bookHomePage.clickNavbarSearchBooksByTitleLink();
		searchByTitlePage.fillSearchFormAndPressSubmitButton(INVALID_TITLE);
		
		assertThat(searchByTitlePage.getValidationErrorMessage())
			.contains("Invalid title");
	}

	@Test
	public void testPocSpringLibraryApplication_searchBookByTitle_withValidButUnusedTitle_shouldOpenBookNotFoundErrorView() {
		BookSearchByTitlePage searchByTitlePage = bookHomePage.clickNavbarSearchBooksByTitleLink();
		MyErrorPage errorPage = (MyErrorPage) 
			searchByTitlePage.fillSearchFormAndPressSubmitButton(UNUSED_TITLE);
		
		assertThat(errorPage.getPageTitle())
			.isEqualTo("Book not found error view");
		assertThat(errorPage.getErrorMessage())
			.isEqualTo(UNUSED_TITLE + ": no book found with this title");
	}

	@Test
	public void testSpringBookshelfApplication_searchBookByTitle_withValidAndUsedTitle_shouldShowRetrievedBooksInATable() {
		setupAddingBookToDatabase(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST);
		
		BookSearchByTitlePage searchByTitlePage = bookHomePage.clickNavbarSearchBooksByTitleLink();
		searchByTitlePage.fillSearchFormAndPressSubmitButton(TITLE);
		
		assertThat(searchByTitlePage.getBookTable())
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, NEW_TITLE, AUTHORS_STRING)
			.doesNotContain(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	/* ---------- SpringBookshelfApplication deleteBook tests ---------- */

	@Test
	public void testSpringBookshelfApplication_deleteBook_whenBookListView_shouldShowUpdatedBookListView() {
		bookHomePage.loginWithValidCredentials();
		BookListPage bookListPage = bookHomePage.clickNavbarShowBookListLink();
		bookListPage.clickDeleteAndThenYes(VALID_ISBN13);
		
		assertThat(bookListPage.getBookTable())
			.doesNotContain(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.contains(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	@Test
	public void testSpringBookshelfApplication_deleteBook_whenBookSearchByIsbnView_shouldShowUpdatedBookListView() {
		bookHomePage.loginWithValidCredentials();
		BookSearchByIsbnPage searchByIsbnPage = bookHomePage.clickNavbarSearchBookByIsbnLink();
		searchByIsbnPage.fillSearchFormAndPressSubmitButton(VALID_ISBN13_WITH_HYPHENS);
		BookListPage bookListPage = searchByIsbnPage.clickDeleteAndThenYes(VALID_ISBN13);
		
		assertThat(bookListPage.getBookTable())
			.doesNotContain(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.contains(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	@Test
	public void testSpringBookshelfApplication_deleteBook_whenBookSearchByTitleView_shouldShowUpdatedBookListView() {
		setupAddingBookToDatabase(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST);
		
		bookHomePage.loginWithValidCredentials();
		BookSearchByTitlePage searchByTitlePage = bookHomePage.clickNavbarSearchBooksByTitleLink();
		searchByTitlePage.fillSearchFormAndPressSubmitButton(TITLE);
		BookListPage bookListPage = searchByTitlePage.clickDeleteAndThenYes(VALID_ISBN13);
		
		assertThat(bookListPage.getBookTable())
			.doesNotContain(VALID_ISBN13_WITHOUT_FORMATTING)
			.contains(
				VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2, 
				NEW_VALID_ISBN13_WITHOUT_FORMATTING, NEW_TITLE, AUTHORS_STRING);
	}

	/* ---------- Helper methods ---------- */

	private void setupAddingBookToDatabase(long isbn, String title, List<String> authors) {
		mongoClient.getDatabase(db_name).getCollection(DB_COLLECTION)
			.insertOne(new Document()
				.append("_id", isbn)
				.append("title", title)
				.append("authors", authors));
	}

}
