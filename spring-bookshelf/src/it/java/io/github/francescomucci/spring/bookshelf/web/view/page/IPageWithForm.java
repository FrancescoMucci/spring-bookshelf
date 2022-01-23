package io.github.francescomucci.spring.bookshelf.web.view.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface IPageWithForm {

	public default void clearAndThenfillFormInput(MyPage page, String inputName, String inputValue) {
		WebElement formInput = page.webDriver.findElement(By.name(inputName));
		formInput.clear();
		formInput.sendKeys(inputValue);
	}

	public default MyPage pressSubmitButton(MyPage page) {
		page.webDriver.findElement(By.name("submit-button")).click();
		return page.nextPage();
	}

	public default String getInputValue(MyPage page, String inputName) {
		WebElement formInput = page.webDriver.findElement(By.name(inputName));
		return formInput.getAttribute("value");
	}

	public default String getMessage(MyPage page, String messageId) {
		return page.webDriver.findElement(By.id(messageId)).getText();
	}

}
