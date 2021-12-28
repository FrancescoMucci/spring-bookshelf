package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.service.BookService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MyBookWebController.class)
public class MyBookWebControllerTest {

	@MockBean
	private BookService bookService;

	@Autowired
	private MockMvc mvc;

	/* ---------- getBookHomeView tests ---------- */

	@Test
	public void testWebController_getBookHomeView_whenHomeUri() throws Exception {
		mvc.perform(get(URI_HOME))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_HOME));
	}

	@Test
	public void testWebController_getBookHomeView_whenBookHomeUri() throws Exception {
		mvc.perform(get(URI_BOOK_HOME))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_HOME));
	}

	@Test
	public void testWebController_getBookHomeView_whenLoginUri() throws Exception {
		mvc.perform(get(URI_LOGIN))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_HOME));
	}

	/* ---------- getBookListView tests ---------- */

	@Test
	public void testWebController_getBookListView_whenDbIsEmpty_shouldAddEmptyMessaggeToModel() throws Exception {
		when(bookService.getAllBooks())
			.thenReturn(Collections.emptyList());
	
		mvc.perform(get(URI_BOOK_LIST))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_LIST))
			.andExpect(model().attribute(MODEL_EMPTY_MESSAGE, MESSAGE_EMPTY_DB));
	}

	@Test
	public void testWebController_getBookListView_whenDbIsNotEmpty_shouldAddBookListToModel() throws Exception {
		List<Book> bookList = asList(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST), new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2));
		when(bookService.getAllBooks())
			.thenReturn(bookList);
	
		mvc.perform(get(URI_BOOK_LIST))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_LIST))
			.andExpect(model().attribute(MODEL_BOOKS, bookList));
	}

}
