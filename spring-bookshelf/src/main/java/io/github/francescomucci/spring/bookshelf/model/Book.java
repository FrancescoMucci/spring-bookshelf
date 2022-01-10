package io.github.francescomucci.spring.bookshelf.model;

import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Book")
public class Book {

	@Id
	private Long isbn;

	private String title;
	private List<String> authors;

	public Book(Long isbn, String title, List<String> authors) {
		this.isbn = isbn;
		this.title = title;
		this.authors = authors;
	}

	public Long getIsbn() {
		return isbn;
	}

	public void setIsbn(Long isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		return "Book [isbn=" + isbn + ", title=" + title + ", authors=" + authors + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authors, isbn, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(authors, other.authors) && Objects.equals(isbn, other.isbn)
				&& Objects.equals(title, other.title);
	}

}
