package io.github.francescomucci.spring.bookshelf.web.dto;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;

public interface BookDataMapper {

	public Book toBook(BookData bookData);
	
	public BookData toBookData(Book book);

}
