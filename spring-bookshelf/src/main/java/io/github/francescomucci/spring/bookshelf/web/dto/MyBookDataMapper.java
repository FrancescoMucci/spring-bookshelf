package io.github.francescomucci.spring.bookshelf.web.dto;

import static java.util.Arrays.asList;

import org.springframework.stereotype.Component;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;

@Component("BookDataMapper")
public class MyBookDataMapper implements BookDataMapper {

	public BookData toBookData(Book book) {
		if (book == null)
			return null;
		
		return new BookData(
			book.getIsbn().toString(),
			book.getTitle(),
			book.getAuthors().toString()
				.replace("[", "")
				.replace("]", ""));
	}

	public Book toBook(BookData bookData) {
		if (bookData == null) 
			return null;
		
		return new Book(
			bookData.toLong(),
			bookData.getTitle(),
			asList(bookData.getAuthors().split(", ")));
	}

}
