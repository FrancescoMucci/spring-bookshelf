package io.github.francescomucci.spring.bookshelf.web.view;

public final class BookViewTestingHelperMethods {

	private BookViewTestingHelperMethods() {
		throw new IllegalStateException("Utility class");
	}

	public static String removeWindowsCR(String s) {
		return s.replace("/r", "");
	}

}
