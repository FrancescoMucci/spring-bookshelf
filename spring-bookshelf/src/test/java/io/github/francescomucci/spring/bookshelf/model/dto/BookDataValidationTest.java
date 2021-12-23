package io.github.francescomucci.spring.bookshelf.model.dto;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class BookDataValidationTest {

	private static Validator validator;

	@BeforeClass
	public static void classSetup() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	/* ----- Tests for ISBN-13 validation ----- */

	@Test
	public void testBookData_validation_whenInvalidIsbn13_shouldReturnISBNConstraintViolation() {
		BookData bookData = new BookData(INVALID_ISBN13_WITHOUT_FORMATTING, TITLE, AUTHORS_STRING);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("isbn");
			assertThat(violation.getMessage())
				.isEqualTo("Invalid ISBN-13; check the advice box to understand how ISBN-13 works");
		});
	}

	@Test
	public void testBookData_validation_whenBlankIsbn_shouldReturnISBNConstraintViolation() {
		BookData bookData = new BookData(" ", TITLE, AUTHORS_STRING);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("isbn");
			assertThat(violation.getMessage())
				.isEqualTo("Invalid ISBN-13; check the advice box to understand how ISBN-13 works");
		});
	}

	@Test
	public void testBookData_validation_whenNullIsbn_shouldReturnNotNullConstraintViolationForIsbnField() {
		BookData bookData = new BookData(null, TITLE, AUTHORS_STRING);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("isbn");
			assertThat(violation.getMessage())
				.isEqualTo("Please fill out this field");
		});
	}

	/* ----- Tests for title validation ----- */

	@Test
	public void testBookData_validation_whenBlankTitle_shouldReturnNotBlankConstraintViolationForTitleField() {
		BookData bookData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, " ", AUTHORS_STRING);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("title");
			assertThat(violation.getMessage())
				.isEqualTo("Please fill out this field");
		});
	}

	@Test
	public void testBookData_validation_whenNullTitle_shouldReturnNotBlankConstraintViolationForTitleField() {
		BookData bookData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, null, AUTHORS_STRING);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("title");
			assertThat(violation.getMessage())
				.isEqualTo("Please fill out this field");
		});
	}

	@Test
	public void testBookData_validation_whenInvalidTitle_shouldReturnTitlePatternConstraintViolation() {
		BookData bookData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, INVALID_TITLE, AUTHORS_STRING);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("title");
			assertThat(violation.getMessage())
				.isEqualTo("Invalid title; the allowed special characters are: & , : . ! ?");
		});
	}

	/* ----- Tests for authors validation ----- */

	@Test
	public void testBookData_validation_whenBlankAuthors_shouldReturnNotBlankConstraintViolationForAuthorsField() {
		BookData bookData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, " ");
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("authors");
			assertThat(violation.getMessage())
				.isEqualTo("Please fill out this field");
		});
	}

	@Test
	public void testBookData_validation_whenNullAuthors_shouldReturnNotBlankConstraintViolationForAuthorsField() {
		BookData bookData = new BookData(VALID_ISBN13_WITHOUT_FORMATTING, TITLE, null);
		
		Set<ConstraintViolation<BookData>> violations = validator.validate(bookData);
		
		assertThat(violations.size()).isOne();
		violations.forEach(violation -> {
			assertThat(violation.getPropertyPath())
				.hasToString("authors");
			assertThat(violation.getMessage())
				.isEqualTo("Please fill out this field");
		});
	}

}
