package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.repository.BookRepository;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;

/* Before run this test, make sure MongoDB is up and running (listening on port 27017) */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MyBookWebControllerIT {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MockMvc mvc;

	@Before
	public void setup() {
		bookRepository.deleteAll();
	}

	@Test
	public void testMyBookWebController_getBookListView_canRetrieveAllBooksUsingTheService() throws Exception {
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		mvc.perform(get(URI_BOOK_LIST))
			.andExpect(model().attribute(MODEL_BOOKS, asList(
				new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING),
				new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2))));
	}

	@Test
	@WithMockAdmin
	public void testMyBookWebController_postDeleteBook_canDeleteBooksUsingTheService() throws Exception {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		Book remainingBook = bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		
		mvc.perform(post("/book/delete/" + VALID_ISBN13_WITHOUT_FORMATTING)
			.with(csrf()));
		
		assertThat(bookRepository.findAll())
			.containsExactly(remainingBook);
	}

	@Test
	@WithMockAdmin
	public void testMyBookWebController_getBookEditView_canRetrieveBookToEditUsingTheService() throws Exception {
		Book toBeRetrievedBook = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		BookData expectedEditFormData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING);
		
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		bookRepository.save(toBeRetrievedBook);
		
		mvc.perform(get("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING))
			.andExpect(model().attribute("bookData", expectedEditFormData));
	}

	@Test
	@WithMockAdmin
	public void testMyBookWebController_postSaveBook_canUpdateBookUsingTheService() throws Exception {
		bookRepository.save(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", NEW_TITLE)
			.param("authors", AUTHORS_STRING));
		
		assertThat(bookRepository.findById(VALID_ISBN13))
			.contains(new Book(VALID_ISBN13, NEW_TITLE, AUTHORS_LIST));
	}

	@Test
	@WithMockAdmin
	public void testMyBookWebController_postAddBook_canAddNewBookUsingTheService() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", UNUSED_ISBN13_WITH_HYPHENS)
			.param("title", UNUSED_TITLE)
			.param("authors", UNUSED_AUTHORS_STRING));
		
		assertThat(bookRepository.findById(UNUSED_ISBN13))
			.contains(new Book(UNUSED_ISBN13, UNUSED_TITLE, UNUSED_AUTHORS_LIST));
	}

	@Test
	public void testMyBookWebController_getBookByIsbn_canRetrieveBookByIsbnUsingTheService() throws Exception {
		Book toBeRetrievedBook = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		BookData foundedBookData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING);
		
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		bookRepository.save(toBeRetrievedBook);
		
		mvc.perform(get(URI_BOOK_GET_BY_ISBN)
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING))
				.andExpect(model().attribute(MODEL_BOOKS, foundedBookData));
	}

	@Test
	public void testMyBookWebController_getBookByTitle_canRetrieveBookByTitleUsingTheService() throws Exception {
		bookRepository.save(new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		bookRepository.save(new Book(NEW_VALID_ISBN13, "Foundation vol.1", AUTHORS_LIST));
		bookRepository.save(new Book(VALID_ISBN13, "Foundation", AUTHORS_LIST));
		
		mvc.perform(get(URI_BOOK_GET_BY_TITLE)
			.param("title", "Foundation"))
				.andExpect(model().attribute(MODEL_BOOKS, asList(
					new BookData(VALID_ISBN13_WITHOUT_FORMATTING, "Foundation", AUTHORS_STRING),
					new BookData(NEW_VALID_ISBN13_WITHOUT_FORMATTING, "Foundation vol.1", AUTHORS_STRING))));
	}

}
