package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import io.github.francescomucci.spring.bookshelf.web.view.page.IPageWithForm;
import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookHomePage extends MyPage implements IPageWithForm {

	private static final String EXPECTED_TITLE = "Book home view";

	@FindBy(id = "remember-me")
	private WebElement rememberMe;

	public BookHomePage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public BookHomePage fillLoginFormAndPressSubmitButton(String username, String password) {
		clearAndThenfillFormInput(this,"username", username);
		clearAndThenfillFormInput(this,"password", password);
		return (BookHomePage) pressSubmitButton(this);
	}

	public BookHomePage loginWithValidCredentials() {
		return fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
	}

	public String getAuthenticationErrorMessage() {
		return getMessage(this,"authentication-error");
	}

	public String getLogoutMessage() {
		return getMessage(this,"logout-message");
	}

	public void checkRemeberMe() {
		rememberMe.click();
	}

}
