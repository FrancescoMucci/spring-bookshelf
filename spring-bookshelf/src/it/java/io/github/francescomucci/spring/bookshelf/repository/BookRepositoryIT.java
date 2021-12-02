package io.github.francescomucci.spring.bookshelf.repository;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.francescomucci.spring.bookshelf.model.Book;

/* Before run this test, make sure MongoDB is up and running (listening on port 27019) */

@RunWith(SpringRunner.class)
@DataMongoTest
public class BookRepositoryIT {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MongoOperations mongoDb;

	@Before
	public void setup() {
		mongoDb.dropCollection(Book.class);
	}

	/*----------- findAll tests ----------*/

	@Test
	public void testBookRepository_findAll_whenMongoDbIsEmpty_shouldReturnEmptyList() {
		List<Book> foundBooks = bookRepository.findAll(Sort.by("title"));
		
		assertThat(foundBooks).isEmpty();
	}

	@Test
	public void testBookRepository_findAll_whenMongoDbIsNotEmpty_shouldReturnOrderedBookList() {
		Book book1 = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		Book book2 = new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		mongoDb.save(book2);
		mongoDb.save(book1);
		
		List<Book> foundBookList = bookRepository.findAll(Sort.by("title"));
		
		assertThat(foundBookList).containsExactly(book1, book2);
	}

	/*----------- findById tests ----------*/

	@Test
	public void testBookRepository_findById_whenSearchedBookNotInMongoDb_shouldReturnEmptyOptional() {
		Book book1 = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		Book book2 = new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		mongoDb.save(book1);
		mongoDb.save(book2);
		
		Optional<Book> foundBookOptional = bookRepository.findById(UNUSED_ISBN13);
		
		assertThat(foundBookOptional).isEmpty();
	}

	@Test
	public void testBookRepository_findById_whenSearchedBookInMongoDb_shouldReturnOptionalContainingTheBook() {
		Book toBeFound = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		Book otherBook = new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2);
		mongoDb.save(toBeFound);
		mongoDb.save(otherBook);
		
		Optional<Book> foundBookOptional = bookRepository.findById(VALID_ISBN13);
		
		assertThat(foundBookOptional).contains(toBeFound);
	}

}
