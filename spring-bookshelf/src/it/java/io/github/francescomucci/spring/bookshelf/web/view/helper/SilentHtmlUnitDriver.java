package io.github.francescomucci.spring.bookshelf.web.view.helper;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;

public class SilentHtmlUnitDriver extends HtmlUnitDriver {

	public SilentHtmlUnitDriver() {
		super();
		this.getWebClient()
			.setCssErrorHandler(new SilentCssErrorHandler());
	}

}
