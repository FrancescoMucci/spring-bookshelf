package io.github.francescomucci.spring.bookshelf.web.view.page.helper;

import org.openqa.selenium.WebDriver;

import io.github.francescomucci.spring.bookshelf.web.view.page.main.BookHomePage;

public final class AuthenticationHelperMethods {

	private AuthenticationHelperMethods() {
		throw new IllegalStateException("Utility class");
	}

	public static void loginWithValidCredentials(WebDriver webDriver, int portNumber) {
		webDriver.get("http://localhost:" + portNumber);
		BookHomePage bookHomePage = new BookHomePage(webDriver);
		bookHomePage.loginWithValidCredentials();
	}

	public static void loginWithValidCredentialsAndRememberMe(WebDriver webDriver, int portNumber) {
		webDriver.get("http://localhost:" + portNumber);
		BookHomePage bookHomePage = new BookHomePage(webDriver);
		bookHomePage.checkRemeberMe();
		bookHomePage.loginWithValidCredentials();
	}

}
