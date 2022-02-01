package controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import error.ModelCheckingException;

public class JPFExecutorTest {
	
	// TODO add something interesting - random labels would be good
	@Disabled
	@Test
	public void testRunner() throws ModelCheckingException {
		Target target = new Target();
		
		JPFExecutor executor = new JPFExecutor(target, null);
		
		executor.runJPF();
	}
}
