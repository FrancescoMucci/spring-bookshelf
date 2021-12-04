package io.github.francescomucci.spring.bookshelf.service;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.exception.BookAlreadyExistException;


@RunWith(MockitoJUnitRunner.class)
public class MyBookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private MyBookService bookService;

	/*----------- getAllBooks tests ----------*/

	@Test
	public void testService_getAllBooks_whenBookRepositoryIsEmpty_shouldReturnEmptyList() {
		when(bookRepository.findAll(Sort.by("title")))
			.thenReturn(Collections.emptyList());
		
		assertThat(bookService.getAllBooks()).isEmpty();
	}

	@Test
	public void testService_getAllBooks_whenBookRepositoryIsNotEmpty_shouldReturnAllBooksInTheRepo() {
		Book book1 = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		Book book2 = new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		when(bookRepository.findAll(Sort.by("title")))
			.thenReturn(asList(book1, book2));
		
		assertThat(bookService.getAllBooks())
			.containsExactly(book1, book2);
	}

	/*----------- getBookByIsbn tests ----------*/

	@Test
	public void testService_getBookByIsbn_whenBookIsNotFound_shouldThrowBookNotFoundException() {
		when(bookRepository.findById(UNUSED_ISBN13))
			.thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> bookService.getBookByIsbn(UNUSED_ISBN13))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessage(UNUSED_ISBN13 + BookNotFoundException.BOOK_NOT_FOUND_MSG);
	}

	@Test
	public void testService_getBookByIsbn_whenBookIsFound_shouldReturnTheFoundedBook() {
		Book book = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		when(bookRepository.findById(VALID_ISBN13))
			.thenReturn(Optional.of(book));
		
		assertThat(bookService.getBookByIsbn(VALID_ISBN13))
			.isSameAs(book);
	}

	/*----------- getBooksByTitle tests ----------*/

	@Test
	public void testService_getBooksByTitle_whenNoBookIsFound_shouldThrowBookNotFoundException() {
		when(bookRepository.findAllByTitleLikeOrderByTitle(UNUSED_TITLE))
			.thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> bookService.getBooksByTitle(UNUSED_TITLE))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessage(UNUSED_TITLE + BookNotFoundException.BOOK_NOT_FOUND_TITLE_MSG);
	}

	@Test
	public void testService_getBookByTitle_whenBookIsFound_shouldReturnTheFoundedBooks() {
		Book book1 = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		Book book2 = new Book(NEW_VALID_ISBN13, TITLE, AUTHORS_LIST);
		when(bookRepository.findAllByTitleLikeOrderByTitle(TITLE))
			.thenReturn(asList(book1, book2));
		
		assertThat(bookService.getBooksByTitle(TITLE))
			.containsExactly(book1, book2);
	}

	/*----------- addNewBook tests ----------*/

	@Test
	public void testService_addNewBook_whenIsbnAlreadyUsed_shouldThrowBookAlreadyExistException() {
		Book existingBook = new Book(ALREADY_USED_ISBN13, NEW_TITLE, AUTHORS_LIST);
		Book newBook = new Book(ALREADY_USED_ISBN13, NEW_TITLE, AUTHORS_LIST);
		when(bookRepository.findById(ALREADY_USED_ISBN13))
			.thenReturn(Optional.of(existingBook));
		
		assertThatThrownBy(() -> bookService.addNewBook(newBook))
			.isInstanceOf(BookAlreadyExistException.class)
			.hasMessage(ALREADY_USED_ISBN13 + BookAlreadyExistException.BOOK_ALREADY_EXIST_MSG);
		
		verify(bookRepository, never()).save(any(Book.class));
	}

	@Test
	public void testService_addNewBook_whenIsbnIsUnused_shouldSaveTheNewBookInTheRepo() {
		Book newBook = new Book(UNUSED_ISBN13, TITLE, AUTHORS_LIST);
		when(bookRepository.findById(UNUSED_ISBN13))
			.thenReturn(Optional.empty());
		when(bookRepository.save(newBook))
			.thenReturn(newBook);
		
		assertThat(bookService.addNewBook(newBook))
			.isSameAs(newBook);
		
		verify(bookRepository).save(newBook);
	}

	/*----------- replaceBook tests ----------*/

	@Test
	public void testService_replaceBook_whenIsbnIsUnused_shouldThrowBookNotFoundException() {
		Book editedBook = new Book(UNUSED_ISBN13, NEW_TITLE, AUTHORS_LIST);
		when(bookRepository.findById(UNUSED_ISBN13))
			.thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> bookService.replaceBook(editedBook))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessage(UNUSED_ISBN13 + BookNotFoundException.BOOK_NOT_FOUND_MSG);
		
		verify(bookRepository, never()).save(any(Book.class));
	}

	@Test
	public void testService_replaceBook_whenIsbnIsUsed_shouldSaveTheReplacementInTheRepo() {
		Book oldBook = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		Book replacementBook = new Book(VALID_ISBN13, NEW_TITLE, AUTHORS_LIST);
		when(bookRepository.findById(VALID_ISBN13))
			.thenReturn(Optional.of(oldBook));
		when(bookRepository.save(any(Book.class)))
			.thenReturn(replacementBook);
	
		assertThat(bookService.replaceBook(replacementBook))
			.isSameAs(replacementBook);
	
		verify(bookRepository).save(replacementBook);
	}

	/*----------- delateBookByIsbn tests ----------*/

	@Test
	public void testService_delateBookByIsbn_whenIsbnIsUnused_shouldThrowBookNotFoundException() {
		when(bookRepository.findById(UNUSED_ISBN13))
			.thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> bookService.delateBookByIsbn(UNUSED_ISBN13))
			.isInstanceOf(BookNotFoundException.class)
			.hasMessage(UNUSED_ISBN13 + BookNotFoundException.BOOK_NOT_FOUND_MSG);
		
		verify(bookRepository, never()).deleteById(anyLong());
	}

	@Test
	public void testService_delateBookByIsbn_whenIsbnIsUsed_shouldDelegateTheDeletionToTheRepo() {
		when(bookRepository.findById(VALID_ISBN13))
			.thenReturn(Optional.of(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST)));
		
		bookService.delateBookByIsbn(VALID_ISBN13);
		
		verify(bookRepository).deleteById(VALID_ISBN13);
	}

}