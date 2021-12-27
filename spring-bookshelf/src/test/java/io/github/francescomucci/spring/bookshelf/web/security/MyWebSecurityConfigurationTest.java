package io.github.francescomucci.spring.bookshelf.web.security;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
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

}
