package controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.RepeatedTest;

import controller.CMD.TargetController;
import model.Target;

public class TargetControllerTest {
	
	
	@RepeatedTest(100)
	public void testConstructor() {
		// create random transition system
		Target expected = new Target();		
		try {
			PrintWriter writer = new PrintWriter("target.properties");
			// target
			writer.println("target = " + expected.getName());
			// path
			writer.println("classpath = " + expected.getPath());
			// close
			writer.close();
		} catch (FileNotFoundException e) {
			fail("Something went wrong with writing to the file Example.tra");
			e.printStackTrace();
		}
		
		Target actual;
		try {
			actual = TargetController.parseTarget(null);
			assertEquals(expected.toString(), actual.toString(), expected.toString() + "\n" + actual.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Something went wrong with reading the file target.properties");
		}
	}
}
