package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FormComponent extends APageObject {

	@FindBy(name = "submit-button")
	private WebElement submitButton;

	public FormComponent(WebDriver webDriver) {
		super(webDriver);
	}

	public void clearAndThenfillFormInput(String inputName, String inputValue) {
		WebElement formInput = webDriver.findElement(By.name(inputName));
		formInput.clear();
		formInput.sendKeys(inputValue);
	}

	public MyPage pressSubmitButton() {
		submitButton.click();
		return new MyPage(webDriver).nextPage();
	}

	public String getInputValue(String inputName) {
		WebElement formInput = webDriver.findElement(By.name(inputName));
		return formInput.getAttribute("value");
	}

	public String getMessage(String messageId) {
		return webDriver.findElement(By.id(messageId)).getText();
	}
}
