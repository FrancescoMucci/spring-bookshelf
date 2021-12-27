package io.github.francescomucci.spring.bookshelf.web.security;

import static io.github.francescomucci.spring.bookshelf.web.security.WebSecurityTestingConstants.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithMockUser;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = VALID_USER_NAME, roles = "ADMIN")
public @interface WithMockAdmin {

}
