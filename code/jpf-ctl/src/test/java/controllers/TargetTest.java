package controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.RepeatedTest;

public class TargetTest {
	
	
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
			// args
			writer.println("target.args = " + expected.getArgs());
			// enumerate random
			writer.println("enumerateRandom = " + expected.getEnumerateRandom());
			// close
			writer.close();
		} catch (FileNotFoundException e) {
			fail("Something went wrong with writing to the file Example.tra");
			e.printStackTrace();
		}
		
		Target actual;
		try {
			actual = new Target(null);
			assertEquals(expected.toString(), actual.toString(), expected.toString() + "\n" + actual.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Something went wrong with reading the file target.properties");
		}
	}
}
