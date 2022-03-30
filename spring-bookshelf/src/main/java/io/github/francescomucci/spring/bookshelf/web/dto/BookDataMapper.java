package io.github.francescomucci.spring.bookshelf.web.dto;

import java.util.List;

import io.github.francescomucci.spring.bookshelf.model.Book;
import io.github.francescomucci.spring.bookshelf.model.dto.BookData;
import io.github.francescomucci.spring.bookshelf.model.dto.IsbnData;

public interface BookDataMapper {

	public Book toBook(BookData bookData);

	public BookData toBookData(Book book);

	public Long toLong(IsbnData isbnData);

	public List<BookData> toBookDataList(List<Book> bookList);

	public BookData updateBookData(BookData bookData, Book book);

}
