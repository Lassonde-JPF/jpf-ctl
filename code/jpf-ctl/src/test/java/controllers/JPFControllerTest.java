package controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import controller.CMD.JPFController;
import error.ModelCheckingException;
import model.Target;

public class JPFControllerTest {
	
	// TODO add something interesting - random labels would be good
	@Disabled
	@Test
	public void testRunner() throws ModelCheckingException {
		Target target = new Target();
		
		JPFController controller = new JPFController(target, null);
		
		controller.runJPF();
	}
}
