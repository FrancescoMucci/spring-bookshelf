package io.github.francescomucci.spring.bookshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import io.github.francescomucci.spring.bookshelf.model.Book;

public interface BookRepository {

	public List<Book> findAll(Sort sort);

	public Optional<Book> findById(long isbn);

	public List<Book> findAllByTitleLikeOrderByTitle(String title);

	public Book save(Book book);

	public void deleteById(long isbn);

}
