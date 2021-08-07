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
			result = ModelChecker.validate("example.Example.one || example.Example.two", "example.Example");
			assertTrue(result);
		} catch (ModelCheckingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void accountTest() {
		boolean result;
		
		try {
			result = ModelChecker.validate("AG ! example.Main.negative", "example.Main");
			assertTrue(result);
		} catch (ModelCheckingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
