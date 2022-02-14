package controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;

import controller.CMD.CTLController;
import formulas.Formula;
import labels.Label;
import model.CTL;
import model.Target;

public class CTLControllerTest {
	/**
	 * Number of times each test is run.
	 */
	private static final int CASES = 100;

	/**
	 * Tests the formula true for a random system.
	 * 
	 * @throws IOException
	 */
	@RepeatedTest(CASES)
	public void testRandom() throws IOException {
		Random r = new Random();

		// Generate Formulas
		int numOfFormulas = r.nextInt(4) + 1;
		Map<String, Formula> formulas = new HashMap<>();
		Set<String> atomicPropositions = new HashSet<>();
		for (int i = 0; i < numOfFormulas; i++) {
			Formula formula = Formula.random();
			formulas.put("f" + (i + 1), formula);
			atomicPropositions.addAll(formula.getAtomicPropositions());
		}

		// Generate Labels
		Map<String, Label> labels = new HashMap<>();
		for (String atomicProposition : atomicPropositions) {
			Label label = Label.random();
			System.out.println(label);
			labels.put(atomicProposition, label);
		}

		// Generate Target
		Target target = new Target();

		// Generate ctl.properties file
		PrintWriter writer = new PrintWriter("ctl.properties");
		for (Entry<String, Label> e : labels.entrySet()) {
			writer.println(e.getKey() + ": " + e.getValue());
		}
		writer.println();
		for (Entry<String, Formula> e : formulas.entrySet()) {
			writer.println(e.getKey() + " = " + e.getValue());
		}
		writer.close();
		
		// Generate expected and actual
		CTL expected = new CTL(labels, formulas);
		CTL actual = CTLController.parseCTL(null, target.getPath());

		// Perform Assertion
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}

}
