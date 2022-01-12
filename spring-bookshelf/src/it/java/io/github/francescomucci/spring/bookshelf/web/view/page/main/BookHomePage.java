package io.github.francescomucci.spring.bookshelf.web.view.page.main;

import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class BookHomePage extends MyPage {

	private static final String EXPECTED_TITLE = "Book home view";

	@FindBy(id = "remember-me")
	private WebElement rememberMe;

	public BookHomePage(WebDriver webDriver) {
		super(webDriver, EXPECTED_TITLE);
	}

	public BookHomePage fillLoginFormAndPressSubmitButton(String username, String password) {
		clearAndThenfillFormInput("username", username);
		clearAndThenfillFormInput("password", password);
		webDriver.findElement(By.name("submit-button")).click();
		return this;
	}

	private void clearAndThenfillFormInput(String inputName, String inputValue) {
		WebElement formInput = webDriver.findElement(By.name(inputName));
		formInput.clear();
		formInput.sendKeys(inputValue);
	}

	public BookHomePage loginWithValidCredentials() {
		return fillLoginFormAndPressSubmitButton(VALID_USER_NAME, VALID_PASSWORD);
	}

	public String getAuthenticationErrorMessage() {
		return getMessage("authentication-error");
	}

	public String getLogoutMessage() {
		return getMessage("logout-message");
	}

	private String getMessage(String messageId) {
		return webDriver.findElement(By.id(messageId)).getText();
	}

	public void checkRemeberMe() {
		rememberMe.click();
	}

}
