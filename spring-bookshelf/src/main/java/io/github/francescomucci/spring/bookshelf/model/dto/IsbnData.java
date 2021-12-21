package io.github.francescomucci.spring.bookshelf.model.dto;

import java.util.Objects;

public class IsbnData {

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

	public Long toLong() {
		return Long.valueOf(isbn.replace("-", "").replace(" ", ""));
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
