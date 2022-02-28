package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;

import formulas.ctl.CTLFormula;
import model.ctl.CTLModelChecker;

public class TransitionSystemTest {

	@RepeatedTest(100)
	void testUpperLowerBoundsConside() {
		Set<CTLFormula> formulas = new HashSet<CTLFormula>();
		Set<String> atomicPropositions = new HashSet<String>();

		for (int i = 0; i < 10; i++) {
			CTLFormula formula = CTLFormula.random();
			atomicPropositions.addAll(formula.getAtomicPropositions());
		}

		TransitionSystem pts = new TransitionSystem(atomicPropositions);
		CTLModelChecker checker = new CTLModelChecker(pts);
		
		for (CTLFormula f : formulas) {
			Result result = checker.check(f);
			assertEquals(result.getUpper(), result.getLower());
		}
	}
}
