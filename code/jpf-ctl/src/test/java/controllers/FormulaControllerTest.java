package controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;

import controller.FormulaController;
import formulas.Formula;
import model.LogicType;

public class FormulaControllerTest {
	/**
	 * Number of times each test is run.
	 */
	private static final int CASES = 100;

	/**
	 * Tests the parseFormula method
	 */
	@RepeatedTest(CASES)
	public void testParseFormula() {
		Formula expected = Formula.random();
		Set<String> labels = expected.getAtomicPropositions();
		Formula actual = FormulaController.parseFormula(labels, expected.toString(), LogicType.CTL);
		assertEquals(expected, actual);
	}
}
