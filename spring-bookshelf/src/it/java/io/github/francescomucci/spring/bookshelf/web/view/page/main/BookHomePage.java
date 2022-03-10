package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import io.github.francescomucci.spring.bookshelf.web.view.page.APageWithForm;

public class BookHomePage extends APageWithForm {

	@FindBy(id = "remember-me")
	private WebElement rememberMe;

	public BookHomePage(WebDriver webDriver) {
		super(webDriver, BOOK_HOME_VIEW);
	}

	public BookHomePage fillLoginFormAndPressSubmitButton(String username, String password) {
		form.clearAndThenfillFormInput("username", username);
		form.clearAndThenfillFormInput("password", password);
		return (BookHomePage) form.pressSubmitButton();
	}

	public BookHomePage loginWithValidCredentials() {
		return fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
	}

	public String getAuthenticationErrorMessage() {
		return form.getMessage("authentication-error");
	}

	public String getLogoutMessage() {
		return form.getMessage("logout-message");
	}

	public void checkRemeberMe() {
		rememberMe.click();
	}

}
