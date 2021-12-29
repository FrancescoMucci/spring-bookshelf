package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;
import io.github.francescomucci.spring.bookshelf.model.dto.group.IsbnConstraints;
import io.github.francescomucci.spring.bookshelf.model.dto.group.TitleConstraints;

@Controller
public interface BookWebController {

	@GetMapping({URI_HOME, URI_BOOK_HOME, URI_LOGIN})
	public String getBookHomeView();

	@GetMapping(URI_BOOK_LIST)
	public String getBookListView(Model model);

	@PostMapping(URI_BOOK_DELETE)
	public String postDeleteBook(@Valid IsbnData isbn, BindingResult result);

	@GetMapping(URI_BOOK_EDIT)
	public String getBookEditView(@Valid IsbnData isbn, BindingResult result, BookData editFormData);

	@PostMapping(URI_BOOK_SAVE)
	public String postSaveBook(@Valid BookData editFormData, BindingResult result);

	@GetMapping(URI_BOOK_NEW)
	public String getBookNewView(BookData addFormData);
	
	@PostMapping(URI_BOOK_ADD)
	public String postAddBook(@Valid BookData addFormData, BindingResult result);

	@GetMapping(URI_BOOK_SEARCH_BY_ISBN)
	public String getBookSearchByIsbnView(BookData searchFormData);

	@GetMapping(URI_BOOK_GET_BY_ISBN)
	public String getBookByIsbn(@Validated(IsbnConstraints.class) BookData searchFormData, BindingResult result, Model model);

	@GetMapping(URI_BOOK_SEARCH_BY_TITLE)
	public String getBookSearchByTitleView(BookData searchFormData);

	@GetMapping(URI_BOOK_GET_BY_TITLE)
	public String getBookByTitle(@Validated(TitleConstraints.class) BookData searchFormData, BindingResult result, Model model);

}
