package controllers;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;

import controller.JPFController;
import error.ModelCheckingException;
import labels.Label;
import model.Target;

public class JPFControllerTest {
	
	@RepeatedTest(100)
	public void testRunner() throws ModelCheckingException {
		Random r = new Random();
		
		// Generate Random Labels
		Set<Label> labels = new HashSet<>();
		int numOfLabels = r.nextInt(9)+1;
		for (int i = 0; i < numOfLabels; i++) {
			labels.add(Label.random());
		}
		
		Target target = new Target();
		JPFController controller = new JPFController(target, labels);
		controller.runJPF();
	}
}
