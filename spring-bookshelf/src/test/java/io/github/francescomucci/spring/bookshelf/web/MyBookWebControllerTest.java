package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.exception.BookNotFoundException;
import io.github.francescomucci.spring.bookshelf.exception.InvalidIsbnException;
import io.github.francescomucci.spring.bookshelf.exception.BookAlreadyExistException;
import io.github.francescomucci.spring.bookshelf.service.BookService;
import io.github.francescomucci.spring.bookshelf.web.security.WithMockAdmin;
import io.github.francescomucci.spring.bookshelf.web.dto.MyBookDataMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MyBookWebController.class)
@Import(MyBookDataMapper.class)
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
	public void testWebController_getBookListView_whenDbIsEmpty_shouldNotAddBookListToModel() throws Exception {
		when(bookService.getAllBooks())
			.thenReturn(Collections.emptyList());
	
		mvc.perform(get(URI_BOOK_LIST))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_LIST))
			.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
	}

	@Test
	public void testWebController_getBookListView_whenDbIsNotEmpty_shouldAddBookListToModel() throws Exception {
		when(bookService.getAllBooks())
			.thenReturn(asList(
				new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
				new Book(VALID_ISBN13_2, TITLE_2, AUTHORS_LIST_2)));
	
		mvc.perform(get(URI_BOOK_LIST))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_LIST))
			.andExpect(model().attribute(MODEL_BOOKS, asList(
				new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING),
				new BookData(VALID_ISBN13_2_WITHOUT_FORMATTING, TITLE_2, AUTHORS_STRING_2))));
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

	/* ---------- getBookEditView tests ---------- */

	@Test
	@WithMockAdmin
	public void testWebController_getBookEditView_whenAdminUserAndIsbnIsInvalid_shouldAddErrorInfoToModelAndReturnInvalidIsbnView() throws Exception {
		mvc.perform(get("/book/edit/" + INVALID_ISBN13_WITHOUT_FORMATTING))
			.andExpect(status().isBadRequest())
			.andExpect(view().name(ERROR_INVALID_ISBN))
			.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.BAD_REQUEST.value()))
			.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.andExpect(model().attribute(MODEL_ERROR_MESSAGE, INVALID_ISBN13_WITHOUT_FORMATTING + InvalidIsbnException.INVALID_ISBN_MSG));
		
		verify(bookService, never()).getBookByIsbn(INVALID_ISBN13);
	}

	@Test
	@WithMockAdmin
	public void testWebController_getBookEditView_whenAdminUserAndIsbnIsValidButUnused_shouldAddErrorInfoToModelAndReturnBookNotFoundView() throws Exception {
		when(bookService.getBookByIsbn(UNUSED_ISBN13))
			.thenThrow(new BookNotFoundException(UNUSED_ISBN13));
		
		mvc.perform(get("/book/edit/" + UNUSED_ISBN13_WITHOUT_FORMATTING))
			.andExpect(status().isNotFound())
			.andExpect(view().name(ERROR_BOOK_NOT_FOUND))
			.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value()))
			.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase()))
			.andExpect(model().attribute(MODEL_ERROR_MESSAGE, UNUSED_ISBN13 + BookNotFoundException.BOOK_NOT_FOUND_MSG));
	}

	@Test
	@WithMockAdmin
	public void testWebController_getBookEditView_whenAdminUserAndIsbnIsValidAndUsed_shouldAddBookFormDataToModel() throws Exception {
		when(bookService.getBookByIsbn(VALID_ISBN13))
			.thenReturn(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		mvc.perform(get("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_EDIT))
			.andExpect(model().attribute("bookData", new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)));
	}

	/* ---------- postSaveBook tests ---------- */

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndNullIsbn_shouldAddErrorInfoToModelAndReturnInvalidIsbnView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", (String) null)
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isBadRequest())
				.andExpect(view().name(ERROR_INVALID_ISBN))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.BAD_REQUEST.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.BAD_REQUEST.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, "null" + InvalidIsbnException.INVALID_ISBN_MSG));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndBlankIsbn_shouldAddErrorInfoToModelAndReturnInvalidIsbnView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", "")
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isBadRequest())
				.andExpect(view().name(ERROR_INVALID_ISBN))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.BAD_REQUEST.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.BAD_REQUEST.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, "" + InvalidIsbnException.INVALID_ISBN_MSG));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndInvalidIsbn_shouldAddErrorInfoToModelAndReturnInvalidIsbnView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", INVALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isBadRequest())
				.andExpect(view().name(ERROR_INVALID_ISBN))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.BAD_REQUEST.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.BAD_REQUEST.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, INVALID_ISBN13_WITHOUT_FORMATTING + InvalidIsbnException.INVALID_ISBN_MSG));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndNullTitle_shouldNotInteractWithServiceAndReturnEditView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", (String) null)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_EDIT))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndBlankTitle_shouldNotInteractWithServiceAndReturnEditView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", "")
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_EDIT))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndInvalidTitle_shouldNotInteractWithServiceAndReturnEditView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", INVALID_TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_EDIT))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "Pattern"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndNullAuthors_shouldNotInteractWithServiceAndReturnEditView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", (String) null))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_EDIT))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "authors", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndBlankAuthors_shouldNotInteractWithServiceAndReturnEditView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", ""))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_EDIT))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "authors", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndInvalidAuthors_shouldNotInteractWithServiceAndReturnEditView() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", INVALID_AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_EDIT))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "authors", "Pattern"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndIsbnIsValidButUnused_shouldAddErrorInfoToModelAndReturnBookNotFoundView() throws Exception {
		BookData editFormData = new BookData(UNUSED_ISBN13_WITHOUT_FORMATTING, UNUSED_TITLE, UNUSED_AUTHORS_STRING);
		Book editedBook = new Book(UNUSED_ISBN13, UNUSED_TITLE, UNUSED_AUTHORS_LIST);
		when(bookService.replaceBook(editedBook))
			.thenThrow(new BookNotFoundException(UNUSED_ISBN13));
		
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", editFormData.getIsbn())
			.param("title", editFormData.getTitle())
			.param("authors", editFormData.getAuthors()))
				.andExpect(status().isNotFound())
				.andExpect(view().name(ERROR_BOOK_NOT_FOUND))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, editFormData.getIsbn() + BookNotFoundException.BOOK_NOT_FOUND_MSG));
	}

	@Test
	@WithMockAdmin
	public void testWebController_postSaveBook_whenAdminUserAndBookFormDataDoNotContainErrors_shouldReplaceBookUsingServiceAndReturnBookListView() throws Exception {
		BookData editFormData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, NEW_TITLE, AUTHORS_STRING);
		Book editedBook = new Book(VALID_ISBN13, NEW_TITLE, AUTHORS_LIST);
		
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", editFormData.getIsbn())
			.param("title", editFormData.getTitle())
			.param("authors", editFormData.getAuthors()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(URI_BOOK_LIST));
		
		verify(bookService).replaceBook(editedBook);
		verifyNoMoreInteractions(bookService);
	}

	/* ---------- getBookNewView tests ---------- */

	@Test
	@WithMockAdmin
	public void testWebController_getBookNewView_whenAdminUser() throws Exception {
		mvc.perform(get(URI_BOOK_NEW))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_NEW))
			.andExpect(model().attribute("bookData", new BookData()));
	}

	/* ---------- postAddBook tests ---------- */

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndNullIsbn_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", (String) null)
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "isbn", "NotNull"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndBlankIsbn_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", "")
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "isbn", "ISBN"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndInvalidIsbn_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", INVALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "isbn", "ISBN"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndNullTitle_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", (String) null)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndBlankTitle_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", "")
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndInvalidTitle_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", INVALID_TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "Pattern"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndNullAuthors_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", (String) null))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "authors", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndBlankAuthors_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", ""))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "authors", "NotBlank"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndInvalidAuthors_shouldNotInteractWithServiceAndReturnNewView() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", INVALID_AUTHORS_STRING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_NEW))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "authors", "Pattern"));
		
		verifyNoInteractions(bookService);
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndIsbnIsValidButAlreadyUsed_shouldAddErrorInfoToModelAndReturnBookAlreadyExistView() throws Exception {
		BookData addFormData = new BookData(ALREADY_USED_ISBN13_WITHOUT_FORMATTING, UNUSED_TITLE, UNUSED_AUTHORS_STRING);
		Book newBook = new Book(ALREADY_USED_ISBN13, UNUSED_TITLE, UNUSED_AUTHORS_LIST);
		when(bookService.addNewBook(newBook))
			.thenThrow(new BookAlreadyExistException(ALREADY_USED_ISBN13));
		
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", addFormData.getIsbn())
			.param("title", addFormData.getTitle())
			.param("authors", addFormData.getAuthors()))
				.andExpect(status().isConflict())
				.andExpect(view().name(ERROR_BOOK_ALREADY_EXIST))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.CONFLICT.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.CONFLICT.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, addFormData.getIsbn() + BookAlreadyExistException.BOOK_ALREADY_EXIST_MSG));
	}

	@Test
	@WithMockAdmin
	public void testWebController_postAddBook_whenAdminUserAndBookFormDataDoNotContainErrors_shouldAddBookUsingServiceAndReturnBookListView() throws Exception {
		BookData addFormData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING);
		Book newBook = new Book(VALID_ISBN13, TITLE, AUTHORS_LIST);
		
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", addFormData.getIsbn())
			.param("title", addFormData.getTitle())
			.param("authors", addFormData.getAuthors()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(URI_BOOK_LIST));
		
		verify(bookService).addNewBook(newBook);
		verifyNoMoreInteractions(bookService);
	}

	/* ---------- getBookSearchByIsbnView tests ---------- */

	@Test
	public void testWebController_getBookSearchByIsbnView() throws Exception {
		mvc.perform(get(URI_BOOK_SEARCH_BY_ISBN))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_SEARCH_BY_ISBN))
			.andExpect(model().attribute("bookData", new BookData()));
	}

	/* ---------- getBookByIsbn tests ---------- */

	@Test
	public void testWebController_getBookByIsbn_whenNullIsbn_shouldNotInteractWithServiceAndReturnSearchByIsbnView() throws Exception {
		mvc.perform(get(URI_BOOK_GET_BY_ISBN)
			.param("isbn", (String) null))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_ISBN))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "isbn", "NotNull"))
				.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
		
		verifyNoInteractions(bookService);
	}

	@Test
	public void testWebController_getBookByIsbn_whenBlankIsbn_shouldNotInteractWithServiceAndReturnSearchByIsbnView() throws Exception {
		mvc.perform(get(URI_BOOK_GET_BY_ISBN)
			.param("isbn", ""))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_ISBN))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "isbn", "ISBN"))
				.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
		
		verifyNoInteractions(bookService);
	}

	@Test
	public void testWebController_getBookByIsbn_whenInvalidIsbn_shouldNotInteractWithServiceAndReturnSearchByIsbnView() throws Exception {
		mvc.perform(get(URI_BOOK_GET_BY_ISBN)
			.param("isbn", INVALID_ISBN13_WITHOUT_FORMATTING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_ISBN))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "isbn", "ISBN"))
				.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
		
		verifyNoInteractions(bookService);
	}

	@Test
	public void testWebController_getBookByIsbn_whenIsbnIsValidButUnused_shouldAddErrorInfoToModelAndReturnBookNotFoundView() throws Exception {
		when(bookService.getBookByIsbn(UNUSED_ISBN13))
			.thenThrow(new BookNotFoundException(UNUSED_ISBN13));
		
		mvc.perform(get(URI_BOOK_GET_BY_ISBN)
			.param("isbn", UNUSED_ISBN13_WITHOUT_FORMATTING))
				.andExpect(status().isNotFound())
				.andExpect(view().name(ERROR_BOOK_NOT_FOUND))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, UNUSED_ISBN13_WITHOUT_FORMATTING + BookNotFoundException.BOOK_NOT_FOUND_MSG));
	}

	@Test
	public void testWebController_getBookByIsbn_whenIsbnIsValid_shouldAddRetrievedBooksToModelAndReturnSearchByIsbnView() throws Exception {
		when(bookService.getBookByIsbn(VALID_ISBN13))
			.thenReturn(new Book(VALID_ISBN13, TITLE, AUTHORS_LIST));
		
		mvc.perform(get(URI_BOOK_GET_BY_ISBN)
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_ISBN))
				.andExpect(model().attribute(MODEL_BOOKS, 
					new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING)));
	}

	/* ---------- getBookSearchByTitleView tests ---------- */

	@Test
	public void testWebController_getBookSearchByTitleView() throws Exception {
		mvc.perform(get(URI_BOOK_SEARCH_BY_TITLE))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_SEARCH_BY_TITLE))
			.andExpect(model().attribute("bookData", new BookData()));
	}

	/* ---------- getBookByTitle tests ---------- */

	@Test
	public void testWebController_getBookByTitle_whenNullTitle_shouldNotInteractWithServiceAndReturnSearchByTitleView() throws Exception {
		mvc.perform(get(URI_BOOK_GET_BY_TITLE)
			.param("title", (String) null))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_TITLE))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "NotBlank"))
				.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
		
		verifyNoInteractions(bookService);
	}

	@Test
	public void testWebController_getBookByTitle_whenBlankTitle_shouldNotInteractWithServiceAndReturnSearchByTitleView() throws Exception {
		mvc.perform(get(URI_BOOK_GET_BY_TITLE)
			.param("title", ""))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_TITLE))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "NotBlank"))
				.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
		
		verifyNoInteractions(bookService);
	}

	@Test
	public void testWebController_getBookByTitle_whenInvalidTitle_shouldNotInteractWithServiceAndReturnSearchByTitleView() throws Exception {
		mvc.perform(get(URI_BOOK_GET_BY_TITLE)
			.param("title", INVALID_TITLE))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_TITLE))
				.andExpect(model().attributeHasFieldErrorCode("bookData", "title", "Pattern"))
				.andExpect(model().attributeDoesNotExist(MODEL_BOOKS));
		
		verifyNoInteractions(bookService);
	}

	@Test
	public void testWebController_getBookByTitle_whenTitleIsValidButNotFound_shouldAddErrorInfoToModelAndReturnBookNotFoundView() throws Exception {
		when(bookService.getBooksByTitle(UNUSED_TITLE))
			.thenThrow(new BookNotFoundException(UNUSED_TITLE));
		
		mvc.perform(get(URI_BOOK_GET_BY_TITLE)
			.param("title", UNUSED_TITLE))
				.andExpect(status().isNotFound())
				.andExpect(view().name(ERROR_BOOK_NOT_FOUND))
				.andExpect(model().attribute(MODEL_ERROR_CODE, HttpStatus.NOT_FOUND.value()))
				.andExpect(model().attribute(MODEL_ERROR_REASON, HttpStatus.NOT_FOUND.getReasonPhrase()))
				.andExpect(model().attribute(MODEL_ERROR_MESSAGE, UNUSED_TITLE + BookNotFoundException.BOOK_NOT_FOUND_TITLE_MSG));
	}

	@Test
	public void testWebController_getBookByTitle_whenTitleIsValidAndFound_shouldAddRetrievedBooksToModelAndReturnSearchByTitleView() throws Exception {
		when(bookService.getBooksByTitle(TITLE))
			.thenReturn(asList(
				new Book(VALID_ISBN13, TITLE, AUTHORS_LIST),
				new Book(NEW_VALID_ISBN13, NEW_TITLE, AUTHORS_LIST)));
		
		mvc.perform(get(URI_BOOK_GET_BY_TITLE)
			.param("title", TITLE))
				.andExpect(status().isOk())
				.andExpect(view().name(VIEW_BOOK_SEARCH_BY_TITLE))
				.andExpect(model().attribute(MODEL_BOOKS, asList(
					new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING),
					new BookData(NEW_VALID_ISBN13_WITHOUT_FORMATTING, NEW_TITLE, AUTHORS_STRING))));
	}

}
