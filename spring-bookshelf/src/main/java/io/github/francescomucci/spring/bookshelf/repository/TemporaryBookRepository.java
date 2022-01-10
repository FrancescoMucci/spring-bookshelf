package io.github.francescomucci.spring.bookshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import io.github.francescomucci.spring.bookshelf.model.Book;

@Repository
public class TemporaryBookRepository implements BookRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	@Override
	public List<Book> findAll(Sort sort) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public Optional<Book> findById(long isbn) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public List<Book> findAllByTitleLikeOrderByTitle(String title) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public Book save(Book book) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	@Override
	public void deleteById(long isbn) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
