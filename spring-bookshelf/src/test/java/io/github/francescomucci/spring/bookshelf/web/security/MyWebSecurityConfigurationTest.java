package io.github.francescomucci.spring.bookshelf.web.security;

import static io.github.francescomucci.spring.bookshelf.BookTestingConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.github.francescomucci.spring.bookshelf.web.BookWebController;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookWebController.class)
public class MyWebSecurityConfigurationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private BookWebController bookWebController;

	/* ---------- Login tests ---------- */

	@Test
	public void testWebSecurityConfiguration_formLogin_whenValidCredentials_shouldAlwaysRedirectToBookHome() throws Exception {
		mvc.perform(formLogin()
			.user(VALID_USER_NAME)
			.password(VALID_PASSWORD))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(URI_BOOK_HOME))
				.andExpect(authenticated().withRoles("ADMIN"));
	}

	/* ---------- Login tests documenting default behavior ---------- */

	@Test
	public void testWebSecurityConfiguration_formLogin_whenInvalidUsername_shouldRedirectToLoginUrlWithErrorParameter() throws Exception {
		mvc.perform(formLogin()
			.user(INVALID_USER_NAME)
			.password(VALID_PASSWORD))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(URI_LOGIN + "?error"))
				.andExpect(unauthenticated());
	}

	@Test
	public void testWebSecurityConfiguration_formLogin_whenInvalidPassword_shouldRedirectToLoginUrlWithErrorParameter() throws Exception {
		mvc.perform(formLogin()
			.user(VALID_USER_NAME)
			.password(INVALID_PASSWORD))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(URI_LOGIN + "?error"))
				.andExpect(unauthenticated());
	}

	@Test
	public void testWebSecurityConfiguration_postLogin_whenInvalidCsrfToken_shouldAlwaysReturn403() throws Exception {
		mvc.perform(post(URI_LOGIN)
			.with(csrf().useInvalidToken())
			.param("username", VALID_USER_NAME)
			.param("password", VALID_PASSWORD))
				.andExpect(status().isForbidden());
	}

	/* ---------- Logout tests documenting default behavior ---------- */

	@Test
	@WithMockAdmin
	public void testWebSecurityConfiguration_logout_whenAdmin_shouldRedirectToLoginUrlWithLogoutParameter() throws Exception {
		mvc.perform(logout())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(URI_LOGIN + "?logout"))
			.andExpect(unauthenticated());
	}

	@Test
	public void testWebSecurityConfiguration_postLogout_whenAnonymousUser_shouldRedirectToLoginUrlWithLogoutParameter() throws Exception {
		mvc.perform(logout())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(URI_LOGIN + "?logout"))
			.andExpect(unauthenticated());
	}

	@Test
	public void testWebSecurityConfiguration_postLogout_whenInvalidCsrfToken_shouldAlwaysReturn403() throws Exception {
		mvc.perform(post("/logout")
			.with(csrf().useInvalidToken()))
			.andExpect(status().isForbidden());
	}

	/* ---------- Protected URI test: postDelete ---------- */

	@Test
	public void testWebSecurityConfiguration_postDeleteBook_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(post("/book/delete/" + VALID_ISBN13_WITHOUT_FORMATTING)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login"));
	}

	/* ---------- Protected URI test: getBookEdit ---------- */

	@Test
	public void testWebSecurityConfiguration_getBookEditView_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(get("/book/edit/" + VALID_ISBN13_WITHOUT_FORMATTING))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost" + URI_LOGIN));
	}

	/* ---------- Protected URI test: postSaveBook ---------- */

	@Test
	public void testWebSecurityConfiguration_postSaveBook_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(post(URI_BOOK_SAVE)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", NEW_TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost" + URI_LOGIN));
	}

	/* ---------- Protected URI test: getBookNewView ---------- */

	@Test
	public void testWebSecurityConfiguration_getBookNewView_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(get(URI_BOOK_NEW))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost" + URI_LOGIN));
	}

	/* ---------- Protected URI test: postAddBook ---------- */

	@Test
	public void testWebSecurityConfiguration_postAddBook_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(post(URI_BOOK_ADD)
			.with(csrf())
			.param("isbn", VALID_ISBN13_WITHOUT_FORMATTING)
			.param("title", TITLE)
			.param("authors", AUTHORS_STRING))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost" + URI_LOGIN));
	}

	/* ---------- Protected URI test: /actuator & /actuator/info ---------- */

	@Test
	public void testWebSecurityConfiguration_getActuator_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(get("/actuator")
			.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost" + URI_LOGIN));
	}

	@Test
	public void testWebSecurityConfiguration_getActuatorInfo_whenAnonymousUser_shouldAlwaysRedirectToLogin() throws Exception {
		mvc.perform(get("/actuator/info")
			.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost" + URI_LOGIN));
	}

}
