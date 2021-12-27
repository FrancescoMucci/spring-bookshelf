package io.github.francescomucci.spring.bookshelf.web.security;

import static io.github.francescomucci.spring.bookshelf.web.BookWebControllerConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String ROLE_ADMIN = "ADMIN";

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
	protected void configure(HttpSecurity http) throws Exception {
		http
			.formLogin()
				.loginPage(URI_LOGIN)
				.defaultSuccessUrl(URI_BOOK_HOME, true);
	}
}