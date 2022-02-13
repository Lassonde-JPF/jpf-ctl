package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;

import formulas.Formula;

public class TransitionSystemTest {

	@RepeatedTest(100)
	void testUpperLowerBoundsConside() {
		Set<Formula> formulas = new HashSet<Formula>();
		Set<String> atomicPropositions = new HashSet<String>();

		for (int i = 0; i < 10; i++) {
			Formula formula = Formula.random();
			atomicPropositions.addAll(formula.getAtomicPropositions());
		}

		TransitionSystem pts = new TransitionSystem(atomicPropositions);
		ModelChecker checker = new ModelChecker(pts);
		
		for (Formula f : formulas) {
			ModelChecker.Result result = checker.check(f);
			assertEquals(result.getUpper(), result.getLower());
		}
	}
}
