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
	/* ----- Book 1: DTO data ----- */
	public static final String VALID_ISBN13_WITH_HYPHENS = "978-8-80-439594-2";
	public static final String VALID_ISBN13_WITH_SPACES = "978 8 80 439594 2";
	public static final String VALID_ISBN13_WITHOUT_FORMATTING = "9788804395942";
	public static final String AUTHORS_STRING = "Isaac Asimov";

	/* ----- Book 2 ----- */
	public static final long VALID_ISBN13_2 = 9781401238964L;
	public static final String TITLE_2 = "Watchmen";
	public static final List<String> AUTHORS_LIST_2 = asList("Alan Moore", "Dave Gibbons");
	/* ----- Book 2: DTO data ----- */
	public static final String VALID_ISBN13_2_WITHOUT_FORMATTING = "9781401238964";
	public static final String AUTHORS_STRING_2 = "Alan Moore, Dave Gibbons";

	/* ----- Book 1 edit ----- */
	public static final long NEW_VALID_ISBN13 = 9780553293357L;
	public static final String NEW_TITLE = "Foundation vol. 1";
	/* ----- Book 1 edit: DTO data ----- */
	public static final String NEW_VALID_ISBN13_WITHOUT_FORMATTING = "9780553293357";

	/* ----- Unused Book ----- */
	public static final long UNUSED_ISBN13 = 9780340960196L;
	public static final String UNUSED_TITLE = "Dune";
	public static final List<String> UNUSED_AUTHORS_LIST = asList("Frank Herbert");
	/* ----- Unused Book: DTO data ----- */
	public static final String UNUSED_ISBN13_WITH_HYPHENS = "978-0-34-096019-6";
	public static final String UNUSED_ISBN13_WITHOUT_FORMATTING = "9780340960196";
	public static final String UNUSED_AUTHORS_STRING = "Frank Herbert";

	/* -----  Already used ISBN13 ----- */
	public static final long ALREADY_USED_ISBN13 =  9788804395942L;
	/* -----  Already used ISBN13: DTO data----- */
	public static final String ALREADY_USED_ISBN13_WITH_HYPHENS = "978-8-80-439594-2";
	public static final String ALREADY_USED_ISBN13_WITHOUT_FORMATTING = "9788804395942";

	/* ----- Invalid ISBN13 ----- */
	public static final long INVALID_ISBN13 = 1234567890123L;
	/* ----- Invalid ISBN13: DTO data ----- */
	public static final String INVALID_ISBN13_WITH_HYPHENS = "123-4-56-789012-3";
	public static final String INVALID_ISBN13_WITH_SPACES = "123 4 56 789012 3";
	public static final String INVALID_ISBN13_WITHOUT_FORMATTING = "1234567890123";
	/* ----- Invalid title and authors: DTO data ----- */
	public static final String INVALID_TITLE = "Foundation OR 1=1";
	public static final String INVALID_AUTHORS_STRING = "Isaac Asimov OR 1=1";

}
