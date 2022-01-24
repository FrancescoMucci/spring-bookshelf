package io.github.francescomucci.spring.bookshelf.web;

public final class BookWebControllerConstants {

	private BookWebControllerConstants() {
		throw new IllegalStateException("Constants class");
	}

	/* URIs */

	public static final String URI_HOME = "/";
	public static final String URI_BOOK_HOME = "/book";
	public static final String URI_LOGIN = "/login";

	public static final String URI_BOOK_NEW = URI_BOOK_HOME + "/new";
	public static final String URI_BOOK_ADD = URI_BOOK_HOME + "/add";

	public static final String URI_BOOK_LIST = URI_BOOK_HOME + "/list";

	public static final String URI_BOOK_EDIT = URI_BOOK_HOME + "/edit/{isbn}";
	public static final String URI_BOOK_SAVE = URI_BOOK_HOME + "/save";

	public static final String URI_BOOK_DELETE = URI_BOOK_HOME + "/delete/{isbn}";

	public static final String URI_BOOK_SEARCH_BY_ISBN = URI_BOOK_HOME + "/searchByIsbn";
	public static final String URI_BOOK_GET_BY_ISBN = URI_BOOK_HOME + "/getByIsbn";

	public static final String URI_BOOK_SEARCH_BY_TITLE = URI_BOOK_HOME + "/searchByTitle";
	public static final String URI_BOOK_GET_BY_TITLE = URI_BOOK_HOME + "/getByTitle";

	public static final String REDIRECT = "redirect:";

	/* Views */

	public static final String DIRECTORY_VIEW = "view/main/";
	public static final String VIEW_BOOK_HOME = DIRECTORY_VIEW + "bookHome";
	public static final String VIEW_BOOK_NEW = DIRECTORY_VIEW + "bookNew";
	public static final String VIEW_BOOK_EDIT = DIRECTORY_VIEW + "bookEdit";
	public static final String VIEW_BOOK_LIST = DIRECTORY_VIEW + "bookList";
	public static final String VIEW_BOOK_SEARCH_BY_ISBN = DIRECTORY_VIEW + "bookSearchByIsbn";
	public static final String VIEW_BOOK_SEARCH_BY_TITLE = DIRECTORY_VIEW + "bookSearchByTitle";

	public static final String DIRECTORY_ERROR = "view/error/";
	public static final String ERROR_BOOK_NOT_FOUND = DIRECTORY_ERROR + "bookNotFound";
	public static final String ERROR_BOOK_ALREADY_EXIST = DIRECTORY_ERROR + "bookAlreadyExist";
	public static final String ERROR_INVALID_ISBN = DIRECTORY_ERROR + "invalidIsbn";

	/* Model attributes */

	public static final String MODEL_BOOKS = "books";
	public static final String MODEL_ERROR_CODE = "status";
	public static final String MODEL_ERROR_REASON = "error";
	public static final String MODEL_ERROR_MESSAGE = "message";

}
