package io.github.francescomucci.spring.bookshelf.web.view.page.error;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import io.github.francescomucci.spring.bookshelf.web.view.page.MyPage;

public class MyErrorPage extends MyPage {

	@FindBy(id = "error-code")
	private WebElement errorCode;

	@FindBy(id = "error-reason")
	private WebElement errorReason;

	@FindBy(id = "error-message")
	private WebElement errorMessage;

	public MyErrorPage(WebDriver webDriver) {
		super(webDriver);
		String actualTitle = getPageTitle();
		if (!actualTitle.contains("error view"))
			throw new IllegalStateException(
				"Expecting an error view, but actual page is: " + actualTitle);
	}

	public String getErrorCode() {
		return errorCode.getText();
	}

	public String getErrorReason() {
		return errorReason.getText();
	}

	public String getErrorMessage() {
		return errorMessage.getText();
	}

}
