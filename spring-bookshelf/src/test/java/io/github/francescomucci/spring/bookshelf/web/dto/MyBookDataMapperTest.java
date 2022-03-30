package io.github.francescomucci.spring.bookshelf.web.dto;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;

public class MyBookDataMapperTest {

	private MyBookDataMapper map;

	@Before
	public void setup() {
		map = new MyBookDataMapper();
	}

	/* ---------- toBook tests ---------- */

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataIsNull_shouldReturnNull() {
		Book mapResult = map.toBook(null);
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataIsbnIsNull_shouldReturnBookWithNullIsbn() {
		BookData bookDataToMap = new BookData(null, TITLE, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult).isEqualTo(new Book(null, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataIsbnIsEmpty_shouldReturnBookWithNullIsbn() {
		BookData bookDataToMap = new BookData("", TITLE, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult).isEqualTo(new Book(null, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataTitleIsNull_shouldReturnBookWithEmptyTitle() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_HYPHENS, null, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult).isEqualTo(new Book(VALID_ISBN13, "", AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataTitleIsEmpty_shouldReturnBookWithEmptyTitle() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_HYPHENS, "", AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult).isEqualTo(new Book(VALID_ISBN13, "", AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataAuthorsIsNull_shouldReturnBookWithEmptyAuthorsList() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_HYPHENS, TITLE, null);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult).isEqualTo(new Book(VALID_ISBN13, TITLE, asList("")));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataAuthorsIsEmpty_shouldReturnBookWithEmptyAuthorsList() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_HYPHENS, TITLE, "");
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult).isEqualTo(new Book(VALID_ISBN13, TITLE, asList("")));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataIsNotNullAndIsbnHasHyphens_shouldReturnCorrespondingBook() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_HYPHENS, TITLE, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult)
			.isEqualTo(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataIsNotNullAndIsbnHasSpaces_shouldReturnCorrespondingBook() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITH_SPACES, TITLE, AUTHORS_STRING);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult)
			.isEqualTo(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
	}

	@Test
	public void testMyBookDataMapper_toBook_whenBookDataIsNotNullAndHasMoreAuthors_shouldReturnCorrespondingBook() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2);
		Book mapResult = map.toBook(bookDataToMap);
		assertThat(mapResult)
			.isEqualTo(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
	}

	/* ---------- toBookData tests ---------- */

	@Test
	public void testMyBookDataMapper_toBookData_whenBookIsNull_shouldReturnNull() {
		BookData mapResult = map.toBookData(null);
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookIsbnIsNull_shouldReturnBookDataWithEmptyIsbn() {
		Book bookToMap = new Book(null, TITLE_2, AUTHORS_LIST_2);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult).isEqualTo(new BookData("", TITLE_2, AUTHORS_STRING_2));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookTitleIsNull_shouldReturngBookDataWithEmptyTitle() {
		Book bookToMap = new Book(VALID_ISBN13_2, null, AUTHORS_LIST_2);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult).isEqualTo(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, "", AUTHORS_STRING_2));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookTitleIsEmpty_shouldReturngBookDataWithEmptyTitle() {
		Book bookToMap = new Book(VALID_ISBN13_2, "", AUTHORS_LIST_2);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult).isEqualTo(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, "", AUTHORS_STRING_2));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookAuthorsIsNull_shouldReturnBookDataWithEmptyAuthors() {
		Book bookToMap = new Book(VALID_ISBN13_2, TITLE_2, null);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult).isEqualTo(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, ""));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookAuthorsIsEmpty_shouldReturnBookDataWithEmptyAuthors() {
		Book bookToMap = new Book(VALID_ISBN13_2, TITLE_2, asList(""));
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult).isEqualTo(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, ""));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookIsNotNullAndHasOneAuthor_shouldReturnCorrespondingBookData() {
		Book bookToMap = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult)
			.isEqualTo(new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING));
	}

	@Test
	public void testMyBookDataMapper_toBookData_whenBookIsNotNullAndHasMoreAuthors_shouldReturnCorrespondingBookData() {
		Book bookToMap = new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		BookData mapResult = map.toBookData(bookToMap);
		assertThat(mapResult)
			.isEqualTo(new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2));
	}

	/* ---------- toLong tests ---------- */

	@Test
	public void testMyBookDataMapper_toLong_whenIsbnDataIsNull_shouldReturnNull() {
		Long mapResult = map.toLong(null);
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toLong_whenIsbnDataContainNullIsbn_shouldReturnNull() {
		Long mapResult = map.toLong(new IsbnData(null));
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toLong_whenIsbnDataContainEmptyIsbn_shouldReturnNull() {
		Long mapResult = map.toLong(new IsbnData(""));
		assertThat(mapResult).isNull();
	}

	@Test
	public void testMyBookDataMapper_toLong_whenIsbnHasHyphens_shouldReturnCorrespondingIsbn() {
		IsbnData isbnDataToMap = new IsbnData(VALID_ISBN13_WITH_HYPHENS);
		Long mapResult = map.toLong(isbnDataToMap);
		assertThat(mapResult).isEqualTo(VALID_ISBN13);
	}

	@Test
	public void testMyBookDataMapper_toLong_whenIsbnHasSpaces_shouldReturnCorrespondingIsbn() {
		IsbnData isbnDataToMap = new IsbnData(VALID_ISBN13_WITH_SPACES);
		Long mapResult = map.toLong(isbnDataToMap);
		assertThat(mapResult).isEqualTo(VALID_ISBN13);
	}

	@Test
	public void testMyBookDataMapper_toLong_whenBookData_shouldReturnCorrespondingIsbn() {
		BookData bookDataToMap = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING);
		Long mapResult = map.toLong(bookDataToMap);
		assertThat(mapResult).isEqualTo(VALID_ISBN13);
	}

	/* ---------- toBookDataList tests ---------- */

	@Test
	public void testMyBookDataMapper_toBookDataList_whenBookLisIsNull_shouldReturnEmptyBookList() {
		List<Book> bookList = null;
		List<BookData> expecedResult = asList();
		List<BookData> mapResult = map.toBookDataList(bookList);
		assertThat(mapResult).isEqualTo(expecedResult);
	}

	@Test
	public void testMyBookDataMapper_toBookDataList_whenBookLisIsEmpty_shouldReturnEmptyBookList() {
		List<Book> bookList = asList();
		List<BookData> expecedResult = asList();
		List<BookData> mapResult = map.toBookDataList(bookList);
		assertThat(mapResult).isEqualTo(expecedResult);
	}

	@Test
	public void testMyBookDataMapper_toBookDataList_whenBookLisIsNotEmpty_shouldReturnCorrespondingBookDataList() {
		List<Book> bookList = asList(
			new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
			new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		List<BookData> expecedResult = asList(
			new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING),
			new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2));
		List<BookData> mapResult = map.toBookDataList(bookList);
		assertThat(mapResult).isEqualTo(expecedResult);
	}

}
