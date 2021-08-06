package ctl;

import java.io.IOException;

import org.junit.Test;

import algo.ModelChecker;

public class FullStackTest {

	@Test
	public void exampleTest() throws IOException {
		ModelChecker mC = new ModelChecker("algo.JavaFields.p1 && algo.javaFields.p2", "Example.class");
		
		mC.check();
	}
}
