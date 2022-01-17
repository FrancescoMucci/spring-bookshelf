package io.github.francescomucci.spring.bookshelf;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

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
import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;
import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookListPage;

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
		
		mongoClient.getDatabase(db_name).drop();
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
		
		webDriver.close();
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
		
		webDriver.close();
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
		BookListPage bookListPage = (BookListPage) bookHomePage.clickNavbarShowBookListLink();
		
		assertThat(bookListPage.getHeaderContent())
			.contains("Empty database");
	}

	@Test
	public void testSpringBookshelfApplication_showBookList_whenSomeBookAdded_shouldShowBooksInATables() {
		setupAddingBookToDatabase(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		setupAddingBookToDatabase(
			new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		
		BookListPage bookListPage = (BookListPage) bookHomePage.clickNavbarShowBookListLink();
		
		assertThat(bookListPage.getBookTable())
			.contains(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)
			.contains(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
	}

	/* ---------- Helper methods ---------- */

	private void setupAddingBookToDatabase(Book book) {
		mongoClient.getDatabase(db_name).getCollection(DB_COLLECTION)
			.insertOne(new Document()
				.append("_id", book.getIsbn())
				.append("title", book.getTitle())
				.append("authors", book.getAuthors()));
	}

}
