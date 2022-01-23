package io.github.francescomucci.spring.bookshelf.web.view.page;

public final class BookFormConstants {

	private BookFormConstants() {
		throw new IllegalStateException("Constants class");
	}

	public static final String INPUT_ISBN = "isbn";
	public static final String INPUT_TITLE = "title";
	public static final String INPUT_AUTHORS = "authors";

	public static final String VALIDATION_ERROR_SUFFIX = "-validation-error";

}
