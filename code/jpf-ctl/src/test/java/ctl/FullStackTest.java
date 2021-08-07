package ctl;

import org.junit.jupiter.api.Test;

import algo.ModelChecker;
import error.ModelCheckingException;

public class FullStackTest {

	@Test
	public void exampleTest() {
		
		try {
			boolean result = ModelChecker.validate("algo.Example.one || algo.Example.two", "algo.Example");
		} catch (ModelCheckingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
