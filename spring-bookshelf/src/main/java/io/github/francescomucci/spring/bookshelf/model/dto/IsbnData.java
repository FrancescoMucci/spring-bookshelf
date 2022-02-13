package io.github.francescomucci.spring.bookshelf.model.dto;

import static org.hibernate.validator.constraints.ISBN.Type.ISBN_13;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.ISBN;

import io.github.francescomucci.spring.bookshelf.model.dto.group.IsbnConstraints;

public class IsbnData {

	@NotNull(message = "{Blank.Field.Message}", groups = {Default.class, IsbnConstraints.class})
	@ISBN(type = ISBN_13, message = "{Invalid.ISBN.Message}", groups = {Default.class, IsbnConstraints.class})
	private String isbn;

	public IsbnData() {
	}

	public IsbnData(String isbn) {
		this.isbn = isbn;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	@Override
	public String toString() {
		return "IsbnData [isbn=" + isbn + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(isbn);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IsbnData other = (IsbnData) obj;
		return Objects.equals(isbn, other.isbn);
	}

}
