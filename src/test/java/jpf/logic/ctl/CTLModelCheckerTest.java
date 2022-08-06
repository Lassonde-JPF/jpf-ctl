/*
 * Copyright (C)  2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package jpf.logic.ctl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;

import jpf.logic.PartialTransitionSystem;
import jpf.logic.Result;

/**
 * Tests the ModelChecker class.
 * 
 * @author Franck van Breugel
 */
public class CTLModelCheckerTest {
	/**
	 * Number of times each test is run.
	 */
	private static final int CASES = 10000;

	/**
	 * Tests the formula true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testTrue() {
		CTLFormula formula = new True();
		PartialTransitionSystem system = new PartialTransitionSystem();
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates());
		CTLModelChecker model = new CTLModelChecker();
		model.setPartialTransitionSystem(system);
		Result result = model.check(formula);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, system.toString());
	}

	/**
	 * Tests the formula false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testFalse() {
		CTLFormula formula = new False();
		PartialTransitionSystem system = new PartialTransitionSystem();
		BitSet expected = new BitSet();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(formula);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, system.toString());
	}

	/**
	 * Tests an atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testAlias() {
		String name = "label";
		CTLFormula formula = new Alias(name);
		PartialTransitionSystem system = new PartialTransitionSystem(formula.getAliases());
		BitSet expected = new BitSet();
		if (system.getLabelling().containsKey(name)) {
			expected = system.getLabelling().get(name);
		} 
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(formula);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, system.toString());
	}

	/**
	 * Tests the formula not f, where f is a random formula, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testNot() {
		CTLFormula formula = CTLFormula.random();
		PartialTransitionSystem system = new PartialTransitionSystem(formula.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result expectedResult = model.check(formula);
		BitSet expected = expectedResult.getUpper();
		expected.flip(0, system.getNumberOfStates());
		CTLFormula not = new Not(formula);
		Result actualResult = model.check(not);
		BitSet actual = actualResult.getLower();
		assertEquals(expected, actual, "\n" + not.toString() + "\n" + system.toString());

		expected = expectedResult.getLower();
		expected.flip(0, system.getNumberOfStates());
		actual = actualResult.getUpper();
		assertEquals(expected, actual, "\n" + not.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula f && g, where f and g are random formulas, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testAnd() {
		CTLFormula left = CTLFormula.random();
		CTLFormula right = CTLFormula.random();
		Set<String> atomicPropositions = left.getAliases();
		atomicPropositions.addAll(right.getAliases());
		PartialTransitionSystem system = new PartialTransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		BitSet expected = expectedLeft;
		expected.and(expectedRight);
		CTLFormula and = new And(left, right);
		Result result = model.check(and);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, and.toString() + "\n" + system.toString());

		expectedLeft = resultLeft.getUpper();
		expectedRight = resultRight.getUpper();
		expected = expectedLeft;
		expected.and(expectedRight);
		actual = result.getUpper();
		assertEquals(expected, actual, and.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula f || g, where f and g are random formulas, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testOr() {
		CTLFormula left = CTLFormula.random();
		CTLFormula right = CTLFormula.random();
		Set<String> atomicPropositions = left.getAliases();
		atomicPropositions.addAll(right.getAliases());
		PartialTransitionSystem system = new PartialTransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		BitSet expected = expectedLeft;
		expected.or(expectedRight);
		CTLFormula or = new Or(left, right);
		Result result = model.check(or);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, or.toString() + "\n" + system.toString());

		expectedLeft = resultLeft.getUpper();
		expectedRight = resultRight.getUpper();
		expected = expectedLeft;
		expected.or(expectedRight);
		actual = result.getUpper();
		assertEquals(expected, actual, or.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula f => g, where f and g are random formulas, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testImplies() {
		CTLFormula left = CTLFormula.random();
		CTLFormula right = CTLFormula.random();
		Set<String> atomicPropositions = left.getAliases();
		atomicPropositions.addAll(right.getAliases());
		PartialTransitionSystem system = new PartialTransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		expectedLeft.flip(0, system.getNumberOfStates());
		BitSet expected = expectedLeft;
		expected.or(expectedRight);
		CTLFormula implies = new Implies(left, right);
		Result result = model.check(implies);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, implies.toString() + "\n" + system.toString());

		expectedLeft = resultLeft.getUpper();
		expectedLeft.flip(0, system.getNumberOfStates());
		expectedRight = resultRight.getUpper();
		expected = expectedLeft;
		expected.or(expectedRight);
		actual = result.getUpper();
		assertEquals(expected, actual, implies.simplify().toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula f <=> g, where f and g are random formulas, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testIff() {
		CTLFormula left = CTLFormula.random();
		CTLFormula right = CTLFormula.random();
		Set<String> atomicPropositions = left.getAliases();
		atomicPropositions.addAll(right.getAliases());
		PartialTransitionSystem system = new PartialTransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(); 
		model.setPartialTransitionSystem(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		BitSet positive = expectedLeft;
		positive.and(expectedRight);
		expectedLeft = resultLeft.getUpper();
		expectedLeft.flip(0, system.getNumberOfStates());
		expectedRight = resultRight.getUpper();
		expectedRight.flip(0, system.getNumberOfStates());
		BitSet negative = expectedLeft;
		negative.and(expectedRight);
		BitSet expected = positive;
		expected.or(negative);
		CTLFormula iff = new Iff(left, right);
		Result result = model.check(iff);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, iff.toString() + "\n" + system.toString() + "\n" + model.check(new Implies(left, right)).getLower() + "\n" + model.check(new Implies(right, left)).getLower() + "\n===\n");

		expectedLeft = resultLeft.getUpper();
		expectedRight = resultRight.getUpper();
		positive = expectedLeft;
		positive.and(expectedRight);
		expectedLeft = resultLeft.getLower();
		expectedLeft.flip(0, system.getNumberOfStates());
		expectedRight = resultRight.getLower();
		expectedRight.flip(0, system.getNumberOfStates());
		negative = expectedLeft;
		negative.and(expectedRight);
		expected = positive;
		expected.or(negative);
		actual = result.getUpper();
		assertEquals(expected, actual, iff.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EX true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsNextTrue() {
		ExistsNext existsNext = new ExistsNext(new True());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			expected.set(state, system.getSuccessors().containsKey(state) || system.getPartial().get(state));
		}
		model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(existsNext);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsNext.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EX false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsNextFalse() {
		ExistsNext existsNext = new ExistsNext(new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		Result result = model.check(existsNext);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsNext.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EX atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsNextAlias() {
		String name = "label";
		ExistsNext existsNext = new ExistsNext(new Alias(name));
		PartialTransitionSystem system = new PartialTransitionSystem(existsNext.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		if (system.getLabelling().containsKey(name)) {
			for (int state = 0; state < system.getNumberOfStates(); state++) {
				boolean holds = system.getSuccessors().containsKey(state) // state has successors
						&& system.getLabelling().containsKey(name) // label exists
						&& system.getSuccessors().get(state).intersects(system.getLabelling().get(name)); // one of the successors is labelled with the atomic proposition
				expected.set(state, holds);
			}
		}
		Result result = model.check(existsNext);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsNext.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AX true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllNextTrue() {
		ForAllNext forAllNext = new ForAllNext(new True());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates());
		Result result = model.check(forAllNext);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllNext.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, forAllNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AX false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllNextFalse() {
		ForAllNext forAllNext = new ForAllNext(new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		Result result = model.check(forAllNext);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllNext.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, forAllNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AX atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllNextAlias() {
		String name = "label";
		ForAllNext forAllNext = new ForAllNext(new Alias(name));
		PartialTransitionSystem system = new PartialTransitionSystem(forAllNext.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (!system.getPartial().get(state)) {
				if (system.getSuccessors().containsKey(state) ) {
					if (system.getLabelling().containsKey(name)) {
						BitSet post = system.getSuccessors().get(state);
						if (subset(post, system.getLabelling().get(name))) {
							expected.set(state);
						}
					}
				}
			} 
		}
		Result result = model.check(forAllNext);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllNext.toString() + "\n" + system.toString());

		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				expected.set(state);
			}
		}
		actual = result.getUpper();
		assertEquals(expected, actual, forAllNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EG true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsAlwaysTrue() {
		ExistsAlways existsAlways = new ExistsAlways(new True());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates(), true);
		Result result = model.check(existsAlways);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsAlways.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsAlways.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EG false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsAlwaysFalse() {
		ExistsAlways existsAlways = new ExistsAlways(new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		Result result = model.check(existsAlways);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsAlways.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsAlways.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EG atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsAlwaysAlias() {
		String name = "label";
		ExistsAlways existsAlways = new ExistsAlways(new Alias(name));
		PartialTransitionSystem system = new PartialTransitionSystem(existsAlways.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(existsAlways);
		if (system.getLabelling().containsKey(name)) {
			BitSet actual = result.getLower();
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				assertTrue(system.getLabelling().get(name).get(state), "state " + state + " is not labelled label\n" + system.toString());
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(post.intersects(actual), "state " + state + " has transitions but no transition to a state satisfying EG label\n" + system.toString());
				}
			}
			actual.flip(0, system.getNumberOfStates());
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(!system.getLabelling().get(name).get(state) || subset(post, actual), "state " + state + " is not labelled label\n" + system.toString());
				}
			}

			actual = result.getUpper();
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				assertTrue(system.getLabelling().get(name).get(state), "state " + state + " is not labelled label\n" + system.toString());
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(system.getPartial().get(state) || post.intersects(actual), "state " + state + " is fully explored, has transitions but no transition to a state satisfying EG label\n" + system.toString());
				}
			}
			actual.flip(0, system.getNumberOfStates());
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(!system.getLabelling().get(name).get(state) || subset(post, actual), "state " + state + " is not labelled label\n" + system.toString());
				}
			}
		}
	}

	/**
	 * Tests the formula AG true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllAlwaysTrue() {
		ForAllAlways forAllAlways = new ForAllAlways(new True());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates(), true);
		Result result = model.check(forAllAlways);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllAlways.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, forAllAlways.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AG false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllAlwaysFalse() {
		ForAllAlways forAllAlways = new ForAllAlways(new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		Result result = model.check(forAllAlways);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllAlways.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, forAllAlways.toString() + "\n" + system.toString());
	}
	
	/**
	 * Tests the formula AG atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllAlwaysAlias() {
		String name = "label";
		ForAllAlways forAllAlways = new ForAllAlways(new Alias(name));
		PartialTransitionSystem system = new PartialTransitionSystem(forAllAlways.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(forAllAlways);
		
		BitSet lower = result.getLower();
		BitSet upper = result.getUpper();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			BitSet post;
			if (system.getSuccessors().containsKey(state)) {
				post = system.getSuccessors().get(state);
			} else {
				post = new BitSet();
			}
			if (lower.get(state)) {
				assertTrue(system.getLabelling().get(name).get(state), "state " + state + " is not labelled label\n" + system.toString());
				assertFalse(system.getPartial().get(state), "state " + state + " is partially explored\n" + system);
				assertTrue(subset(post, lower), "post(" + state + ") is not a subset of the lower approximation\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(name).get(state)
						|| !system.getPartial().get(state)
						|| !subset(post, lower));
			}
			
			if (upper.get(state)) {
				assertTrue(system.getLabelling().get(name).get(state), "state " + state + " is not labelled label\n" + system.toString());
				assertTrue(subset(post, upper), "post(" + state + ") is not a subset of the upper approximation\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(name).get(state)
						|| !subset(post, upper));
			}
		}
	}

	/**
	 * Tests the formula EF true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsEventuallyTrue() {
		ExistsEventually existsEventually = new ExistsEventually(new True());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates(), true);
		Result result = model.check(existsEventually);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsEventually.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsEventually.toString() + "\n" + system.toString());
	}
	
	/**
	 * Tests the formula EF false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsEventuallyFalse() {
		ExistsEventually existsEventually = new ExistsEventually(new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		Result result = model.check(existsEventually);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsEventually.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsEventually.toString() + "\n" + system.toString());
	}
	
	/**
	 * Tests the formula EF atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsEventuallyAlias() {
		String name = "label";
		ExistsEventually existsEventually = new ExistsEventually(new Alias(name));
		PartialTransitionSystem system = new PartialTransitionSystem(existsEventually.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(existsEventually);
		
		BitSet lower = result.getLower();
		BitSet upper = result.getUpper();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			BitSet post;
			if (system.getSuccessors().containsKey(state)) {
				post = system.getSuccessors().get(state);
			} else {
				post = new BitSet();
			}
			if (lower.get(state)) {
				assertTrue(system.getLabelling().get(name).get(state)
						|| post.intersects(lower));
			} else {
				assertTrue(!system.getLabelling().get(name).get(state)
						&& !post.intersects(lower));
			}
			
			if (upper.get(state)) {
				assertTrue(system.getLabelling().get(name).get(state)
						|| system.getPartial().get(state)
						|| post.intersects(upper));
			} else {
				assertTrue(!system.getLabelling().get(name).get(state)
						&& !system.getPartial().get(state)
						&& !post.intersects(upper));
			}
		}
	}
	
	/**
	 * Tests the formula AF true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllEventuallyTrue() {
		ForAllEventually forAllEventually = new ForAllEventually(new True());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates(), true);
		Result result = model.check(forAllEventually);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllEventually.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, forAllEventually.toString() + "\n" + system.toString());
	}
	
	/**
	 * Tests the formula AF false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllEventuallyFalse() {
		ForAllEventually forAllEventually = new ForAllEventually(new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		BitSet expected = new BitSet();
		Result result = model.check(forAllEventually);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, forAllEventually.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, forAllEventually.toString() + "\n" + system.toString());
	}
	
	/**
	 * Tests the formula AF atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllEventuallyAlias() {
		String name = "label";
		ForAllEventually forAllEventually = new ForAllEventually(new Alias(name));
		PartialTransitionSystem system = new PartialTransitionSystem(forAllEventually.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(forAllEventually);
		
		BitSet lower = result.getLower();
		BitSet upper = result.getUpper();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			BitSet post;
			if (system.getSuccessors().containsKey(state)) {
				post = system.getSuccessors().get(state);
			} else {
				post = new BitSet();
			}
			if (lower.get(state)) {
				assertTrue(system.getLabelling().get(name).get(state)
						|| (!system.getPartial().get(state) && !post.isEmpty() && subset(post, lower)), "state " + state + " in " + lower + "\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(name).get(state)
						&& (system.getPartial().get(state) || post.isEmpty() || !subset(post, lower)), "state " + state + " not in " + lower + "\n" + system);
			}
			
			if (upper.get(state)) {
				assertTrue(system.getLabelling().get(name).get(state)
						|| (!(!system.getPartial().get(state) && post.isEmpty()) && subset(post, upper)), "state " + state + " in " + upper + "\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(name).get(state)
						&& ((!system.getPartial().get(state) && post.isEmpty()) || !subset(post, upper)), "state " + state + " not in " + upper + "\n" + system);		
			}
		}
	}
	
	/**
	 * Tests the formula f EU true, where f is a random formula, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsUntilTrue() {
		CTLFormula left = CTLFormula.random();
		ExistsUntil existsUntil = new ExistsUntil(left, new True());
		PartialTransitionSystem system = new PartialTransitionSystem(left.getAliases());
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(existsUntil);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsUntil.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsUntil.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula f EU false, where f is a random formula, for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsUntilFalse() {
		ExistsUntil existsUntil = new ExistsUntil(new True(), new False());
		PartialTransitionSystem system = new PartialTransitionSystem();
		BitSet expected = new BitSet();
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(existsUntil);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsUntil.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsUntil.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula atomic proposition EU atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsUntilAlias() {
		String left = "left";
		String right = "right";
		ExistsUntil existsUntil = new ExistsUntil(new Alias(left), new Alias(right));
		PartialTransitionSystem system = new PartialTransitionSystem(existsUntil.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(existsUntil);
		BitSet actual = result.getLower();
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(right) && system.getLabelling().get(right).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(left) && system.getLabelling().get(left).get(state);
			BitSet post = system.getSuccessors().get(state);
			assertTrue(satisfiesRight || (satisfiesLeft && post.intersects(actual)), "state " + state + " does not satisy left EU right\n" + system.toString());
		}
		actual.flip(0, system.getNumberOfStates());
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(right) && system.getLabelling().get(right).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(left) && system.getLabelling().get(left).get(state);
			if (system.getSuccessors().containsKey(state)) {
				BitSet post = system.getSuccessors().get(state);
				assertTrue((!satisfiesRight && !satisfiesLeft) || (!satisfiesRight && satisfiesLeft && subset(post, actual)),"state " + state + " satisfies left EU right\n" + system.toString());
			}
		}

		actual = result.getUpper();
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(right) && system.getLabelling().get(right).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(left) && system.getLabelling().get(left).get(state);
			BitSet post = system.getSuccessors().get(state);
			assertTrue(satisfiesRight || (satisfiesLeft && (system.getPartial().get(state) || post.intersects(actual))), "state " + state + " does not satisy left EU right\n" + system.toString());
		}
		actual.flip(0, system.getNumberOfStates());
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(right) && system.getLabelling().get(right).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(left) && system.getLabelling().get(left).get(state);
			if (system.getSuccessors().containsKey(state)) {
				BitSet post = system.getSuccessors().get(state);
				assertTrue((!satisfiesRight && !satisfiesLeft) || (!satisfiesRight && satisfiesLeft && subset(post, actual)),"state " + state + " satisfies left EU right\n" + system.toString());
			}
		}
	}

	/**
	 * Tests that the lower approximation is a subset of the upper approximation for a random formula and a random system. 
	 */
	@RepeatedTest(CASES)
	public void testUpperSubsetLower() {
		CTLFormula formula = CTLFormula.random();
		PartialTransitionSystem system = new PartialTransitionSystem(formula.getAliases());
		CTLModelChecker model = new CTLModelChecker(); model.setPartialTransitionSystem(system);
		Result result = model.check(formula);
		assertTrue(subset(result.getLower(), result.getUpper()), "lower is not a subset of upper for formula\n" + formula + "\nand system\n" + system);
	}
	
	/**
	 * Tests whether the smaller set is a subset of the larger set.
	 * 
	 * @param smaller a set
	 * @param bigger a set
	 * @return true if the smaller set is a subset of the larger set,
	 * false otherwise
	 */
	private static boolean subset(BitSet smaller, BitSet bigger) {
		BitSet copy = (BitSet) bigger.clone();
		copy.and(smaller);
		return smaller.equals(copy);
	}
}
