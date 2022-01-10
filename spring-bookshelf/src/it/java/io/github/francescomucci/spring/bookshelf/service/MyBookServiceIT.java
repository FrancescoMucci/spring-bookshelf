package io.github.francescomucci.spring.bookshelf.service;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@DataMongoTest
@Import(MyBookService.class)
public class MyBookServiceIT {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MyBookService bookService;

	@Before
	public void setup() {
		bookRepository.deleteAll();
	}

	@Test
	public void testBookService_getAllBooks_canRetrieveAllBooksFromRepository() {
		Book book2 = bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		Book book1 = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		List<Book> retrievedBooks = bookService.getAllBooks();
		
		assertThat(retrievedBooks)
			.containsExactly(book1, book2);
	}

	@Test
	public void testBookService_getBookByIsbn_canRetrieveBookByIsbnFromRepository() {
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		Book toBeRetrievedBook = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		Book retrievedBook = bookService.getBookByIsbn(VALID_ISBN13);
		
		assertThat(retrievedBook)
			.isEqualTo(toBeRetrievedBook);
	}

	@Test
	public void testBookService_getBooksByTitle_canRetrieveBooksByTitleFromRepository() {
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		Book toBeRetrievedBook2 = bookRepository.save(new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
		Book toBeRetrievedBook1 = bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		List<Book> retrievedBooks = bookService.getBooksByTitle(TITLE);
		
		assertThat(retrievedBooks)
			.containsExactly(toBeRetrievedBook1, toBeRetrievedBook2);
	}

	@Test
	public void testBookService_addNewBook_canAddNewBookIntoRepository() {
		Book newBook = new Book(UNUSED_ISBN13, UNUSED_TITLE, UNUSED_AUTHORS_LIST);
		
		Book addedNewBook = bookService.addNewBook(newBook);
		
		assertThat(bookRepository.findById(UNUSED_ISBN13))
			.contains(addedNewBook);
	}

	@Test
	public void testBookService_replaceBook_canUpdateBookIntoRepository() {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		Book updatedBook = bookService.replaceBook(new Book(VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
		
		assertThat(bookRepository.findById(VALID_ISBN13))
			.contains(updatedBook);
	}

	@Test
	public void testBookService_deleteBookByIsbn_canDeleteBookIntoRepository() {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		Book remainingBook = bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		
		bookService.delateBookByIsbn(VALID_ISBN13);
		
		assertThat(bookRepository.findAll())
			.containsExactly(remainingBook);
	}

}
