package ctl;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import algo.ModelChecker;

public class FullStackTest {

	@Test
	public void exampleTest() throws IOException {
		ModelChecker mC = new ModelChecker("algo.JavaFields.p1 && algo.JavaFields.p2", "algo.Example");
	
		mC.check();
	}
}
