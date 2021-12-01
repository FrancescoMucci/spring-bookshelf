package io.github.francescomucci.spring.bookshelf;

import static java.util.Arrays.asList;

import java.util.List;

public final class BookTestingConstants {

	private BookTestingConstants() {
		throw new IllegalStateException("Constants class");
	}

	/* ----- Book 1 ----- */
	public static final long VALID_ISBN13 = 9788804395942L;
	public static final String TITLE = "Foundation";
	public static final List<String> AUTHORS_LIST = asList("Isaac Asimov");

	/* ----- Book 2 ----- */
	public static final long VALID_ISBN13_2 = 9781401238964L;
	public static final String TITLE_2 = "Watchmen";
	public static final List<String> AUTHORS_LIST_2 = asList("Alan Moore", "Dave Gibbons");

	/* ----- Book 1 edit ----- */
	public static final long NEW_VALID_ISBN13 = 9780553293357L;
	public static final String NEW_TITLE = "Foundation vol. 1";

	/* ----- Unused Book ----- */
	public static final long UNUSED_ISBN13 = 9780340960196L;
	public static final String UNUSED_TITLE = "Dune";
	public static final List<String> UNUSED_AUTHORS_LIST = asList("Frank Herbert");

	/* -----  Already used ISBN13 ----- */
	public static final long ALREADY_USED_ISBN13 =  9788804395942L;

}
