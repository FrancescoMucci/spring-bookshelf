package io.github.francescomucci.spring.bookshelf.web.security;

public final class WebSecurityTestingConstants {

	private WebSecurityTestingConstants() {
		throw new IllegalStateException("Constants class");
	}

	/* ----- Credentials ----- */

	public static final String VALID_USER_NAME = "Admin";
	public static final String VALID_PASSWORD = "Password";

	public static final String INVALID_USER_NAME = "Invalid user name";
	public static final String INVALID_PASSWORD = "Invalid password";

}
