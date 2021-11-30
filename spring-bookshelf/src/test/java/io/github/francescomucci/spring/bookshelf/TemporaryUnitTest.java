package io.github.francescomucci.spring.bookshelf;

import static org.junit.Assert.*;

import org.junit.Test;

public class TemporaryUnitTest {

	@Test
	public void test() {
		TemporaryItem temp = new TemporaryItem("Good morning");
		assertEquals("Good morning", temp.sayHello());
	}

}
