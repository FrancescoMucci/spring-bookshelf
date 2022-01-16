package io.github.francescomucci.spring.bookshelf.web.security;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String[] STATIC_RESOURCES = {
		"/webjars/**", 
		"/css/**"
	};
	private static final String[] UNSECURED_URIS = {
		URI_HOME, 
		URI_LOGIN,
		URI_BOOK_HOME, 
		URI_BOOK_LIST, 
		URI_BOOK_SEARCH_BY_ISBN, 
		URI_BOOK_GET_BY_ISBN, 
		URI_BOOK_SEARCH_BY_TITLE, 
		URI_BOOK_GET_BY_TITLE, 
		"/actuator/health"
	};

	private static final String ROLE_ADMIN = "ADMIN";

	private static final String REMEMBER_ME_TOKEN = "spring-bookshelf-remember-me";
	private static final int TOKEN_VALIDITY_SECONDS = 1800; // 30 minutes

	@Value("${admin.username:Admin}")
	private String adminUsername;

	@Value("${admin.password:Password}")
	private String adminPassword;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
			.withUser(adminUsername)
			.password(passwordEncoder.encode(adminPassword))
			.roles(ROLE_ADMIN);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(STATIC_RESOURCES);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers(UNSECURED_URIS).permitAll()
				.anyRequest().hasRole(ROLE_ADMIN)
			.and().formLogin()
				.loginPage(URI_LOGIN)
				.defaultSuccessUrl(URI_BOOK_HOME, true)
			.and().rememberMe()
				.rememberMeCookieName(REMEMBER_ME_TOKEN)
				.tokenValiditySeconds(TOKEN_VALIDITY_SECONDS);
	}
}
