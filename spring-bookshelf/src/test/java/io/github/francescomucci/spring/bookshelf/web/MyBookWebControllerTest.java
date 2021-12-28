package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.exception.InvalidIsbnException;
import io.github.francescomucci.spring.bookshelf.service.BookService;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;

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

	/* ---------- postDeleteBook tests ---------- */

	@Test
	@WithMockAdmin
	public void testWebController_postDeleteBook_whenAdminUserAndIsbnIsInvalid_shouldAddErrorInfoToModelAndReturnInvalidIsbnView() throws Exception {
		mvc.perform(post("/book/delete/" + INVALID_ISBN13_WITHOUT_FORMATTING)
			.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(view().name(ERROR_INVALID_ISBN))
			.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.BAD_REQUEST.value()))
			.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(model().attribute(MODEL_ERROR_MESSAGE, INVALID_ISBN13_WITHOUT_FORMATTING + InvalidIsbnException.INVALID_ISBN_MSG));
		
		verify(bookService, never()).delateBookByIsbn(VALID_ISBN13);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postDeleteBook_whenAdminUserAndIsbnIsValidButUnused_shouldAddErrorInfoToModelAndReturnBookNotFoundView() throws Exception {
		doThrow(new BookNotFoundException(UNUSED_ISBN13))
			.when(bookService).delateBookByIsbn(UNUSED_ISBN13);
		
		mvc.perform(post("/book/delete/" + UNUSED_ISBN13_WITHOUT_FORMATTING)
			.with(csrf()))
			.andExpect(status().isNotFound())
			.andExpect(view().name(ERROR_BOOK_NOT_FOUND))
			.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value()))
			.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase()))
			.andExpect(model().attribute(MODEL_ERROR_MESSAGE, UNUSED_ISBN13 + BookNotFoundException.BOOK_NOT_FOUND_MSG));
	}

	@Test
	@WithMockAdmin
	public void testWebController_postDeleteBook_whenAdminUserAndIsbnIsValidAndUsed_shouldRedirectToBookListView() throws Exception {
		mvc.perform(post("/book/delete/" + VALID_ISBN13_WITHOUT_FORMATTING)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(URI_BOOK_LIST));
		
		verify(bookService).delateBookByIsbn(VALID_ISBN13);
		verifyNoMoreInteractions(bookService);
	}

}
