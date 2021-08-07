package ctl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import algo.ModelChecker;
import error.ModelCheckingException;

public class FullStackTest {

	@Test
	public void exampleTest() {
		boolean result;

		try {
			result = ModelChecker.validate("algo.Example.one || algo.Example.two", "algo.Example");
			assertTrue(result);
		} catch (ModelCheckingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
