package io.github.francescomucci.spring.bookshelf.web.security;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
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

}
