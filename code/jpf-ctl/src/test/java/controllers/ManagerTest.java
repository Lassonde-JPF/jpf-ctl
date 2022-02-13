package controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.RepeatedTest;

import formulas.Formula;
import model.Manager;
import model.TransitionSystem;
import model.ModelChecker.Result;

public class ManagerTest {

	/**
	 * Tests that nothing goes wrong (exceptions, etc.) during the model checking
	 * process
	 * 
	 * @throws TimeoutException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	@RepeatedTest(100)
	public void testRandomEverything() throws InterruptedException, ExecutionException, TimeoutException {

		// Maps and Sets
		Map<String, Formula> formulas = new HashMap<String, Formula>();
		Set<String> atomicPropositions = new HashSet<String>();

		// Generate a mapping of random formulas, collect their APs, build mapping
		for (int i = 0; i < 10; i++) {
			Formula formula = Formula.random();
			String key = "a" + (i + 1);
			formulas.put(key, formula);
			atomicPropositions.addAll(formula.getAtomicPropositions());
		}

		// Generate a Transition System using collected APs
		TransitionSystem pts = new TransitionSystem(atomicPropositions);

		// Generate a Manager
		Manager manager = new Manager(pts, formulas);

		// Perform Validation
		Map<String, Result> results = manager.validateSequentially();

		results = manager.validateParallel((long) 10, TimeUnit.SECONDS);
	}

	public void testValidateSequentially() {

	}

	public void testValidateParallel() {

	}
}
