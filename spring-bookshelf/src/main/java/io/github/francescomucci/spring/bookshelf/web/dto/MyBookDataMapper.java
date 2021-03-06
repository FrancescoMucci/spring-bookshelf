package io.github.francescomucci.spring.bookshelf.web.dto;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;

@Component("BookDataMapper")
public class MyBookDataMapper implements BookDataMapper {

	@Override
	public Book toBook(BookData bookData) {
		if (bookData == null) 
			return null;
		
		return new Book(
			isbnToLong(bookData.getIsbn()),
			titleToString(bookData.getTitle()),
			authorsToList(bookData.getAuthors()));
	}

	@Override
	public BookData toBookData(Book book) {
		if (book == null)
			return null;
		
		return new BookData(
			isbnToString(book.getIsbn()),
			titleToString(book.getTitle()),
			authorsToString(book.getAuthors()));
	}

	@Override
	public Long toLong(IsbnData isbnData) {
		if (isbnData == null) 
			return null;
		
		return isbnToLong(isbnData.getIsbn());
	}

	@Override
	public List<BookData> toBookDataList(List<Book> bookList) {
		if (bookList == null)
			return asList();
		
		return bookList.stream()
			.map(this::toBookData)
			.collect(Collectors.toList());
	}

	@Override
	public BookData updateBookData(BookData bookData, Book book) {
		if (bookData == null || book == null)
			return null;
		
		bookData.setIsbn(isbnToString(book.getIsbn()));
		bookData.setTitle(titleToString(book.getTitle()));
		bookData.setAuthors(authorsToString(book.getAuthors()));
		return bookData;
	}

	private Long isbnToLong(String isbn) {
		if (isbn == null || isbn.isEmpty())
			return null;
		
		return Long.valueOf(isbn
			.replace("-", "")
			.replace(" ", ""));
	}

	private String titleToString(String title) {
		if (title == null)
			return "";
		
		return title;
	}

	private List<String> authorsToList(String authors) {
		if (authors == null)
			return asList("");
		
		return asList(authors.split(", "));
	}

	private String isbnToString(Long isbn) {
		if (isbn == null)
			return "";
		
		return isbn.toString();
	}

	private String authorsToString(List<String> authors) {
		if (authors == null)
			return "";
		
		return authors.toString()
			.replace("[", "")
			.replace("]", "");
	}

}
