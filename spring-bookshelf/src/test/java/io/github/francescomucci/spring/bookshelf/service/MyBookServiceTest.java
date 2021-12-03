package io.github.francescomucci.spring.bookshelf.service;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;


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

}