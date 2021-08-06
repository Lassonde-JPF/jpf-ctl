package ctl;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import algo.ModelChecker;

public class FullStackTest {

	@Test
	public void exampleTest() throws IOException {
		ModelChecker mC = new ModelChecker("algo.Example.one || algo.Example.two", "algo.Example");
	
		mC.check();
	}
}
