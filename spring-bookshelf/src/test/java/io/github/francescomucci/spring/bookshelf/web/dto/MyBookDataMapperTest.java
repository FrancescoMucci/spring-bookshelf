package io.github.francescomucci.spring.bookshelf.web.dto;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;

public class MyBookDataMapperTest {

	private MyBookDataMapper map;

	@Before
	public void setup() {
		map = new MyBookDataMapper();
	}

	/* ---------- toBook tests ---------- */

	@Test
	public void testMyBookDataMapper_toBook_whenBookIsNull_shouldReturnNull() {
		Book mapResult = map.toBook(null);
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookIsNotNullAndIsbnHasHyphens_shouldReturnCorrespondingBookData() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_HYPHENS, TITLE, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult)
			.isEqualTo(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookIsNotNullAndIsbnHasSpaces_shouldReturnCorrespondingBookData() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_SPACES, TITLE, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult)
			.isEqualTo(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookIsNotNullAndHasMoreAuthors_shouldReturnCorrespondingBookData() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult)
			.isEqualTo(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
	}

	/* ---------- toBookData tests ---------- */

	@Test
	public void testMyBookDataMapper_toBookData_whenBookDataIsNull_shouldReturnNull() {
		BookData mapResult = map.toBookData(null);
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookDataIsNotNullAndHasOneAuthor_shouldReturnCorrespondingBook() {
		Book bookToMap = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult)
			.isEqualTo(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookDataIsNotNullAndHasMoreAuthors_shouldReturnCorrespondingBook() {
		Book bookToMap = new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult)
			.isEqualTo(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2));
	}

}
