package io.github.francescomucci.spring.bookshelf.model.dto;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

import io.github.francescomucci.spring.bookshelf.model.dto.group.TitleConstraints;
import io.github.francescomucci.spring.bookshelf.model.dto.group.AuthorsConstraints;

public class BookData extends IsbnData {

	@NotBlank(message = "{Blank.Field.Message}", groups = {Default.class, TitleConstraints.class})
	@Pattern(regexp = "^$|[a-zA-Z0-9&,:.!? ]+$", message = "{Invalid.Title.Message}", groups = {Default.class, TitleConstraints.class})
	private String title;

	@NotBlank(message = "{Blank.Field.Message}", groups = {Default.class, AuthorsConstraints.class})
	@Pattern(regexp = "^$|[a-zA-Z, ]+$", message = "{Invalid.Authors.Message}", groups = {Default.class, AuthorsConstraints.class})
	private String authors;

	public BookData() {
		super();
	}

	public BookData(String isbn, String title, String authors) {
		super(isbn);
		this.title = title;
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		return "BookData [isbn=" + getIsbn() + ", title=" + title + ", authors=" + authors + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authors, getIsbn(), title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookData other = (BookData) obj;
		return Objects.equals(authors, other.authors) && Objects.equals(getIsbn(), other.getIsbn())
				&& Objects.equals(title, other.title);
	}

}
