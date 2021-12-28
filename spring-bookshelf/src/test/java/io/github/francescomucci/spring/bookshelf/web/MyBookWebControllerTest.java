package io.github.francescomucci.spring.bookshelf.web;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MyBookWebController.class)
public class MyBookWebControllerTest {

	@Autowired
	private MockMvc mvc;

	/* ---------- getBookHomeView tests ---------- */

	@Test
	public void testWebController_getBookHomeView_whenHomeUri() throws Exception {
		mvc.perform(get(URI_HOME))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_HOME));
	}

	@Test
	public void testWebController_getBookHomeView_whenBookHomeUri() throws Exception {
		mvc.perform(get(URI_BOOK_HOME))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_HOME));
	}

	@Test
	public void testWebController_getBookHomeView_whenLoginUri() throws Exception {
		mvc.perform(get(URI_LOGIN))
			.andExpect(status().isOk())
			.andExpect(view().name(VIEW_BOOK_HOME));
	}

}
