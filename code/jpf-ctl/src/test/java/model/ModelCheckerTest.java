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

package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.RepeatedTest;

import formulas.*;
import formulas.ctl.And;
import formulas.ctl.AtomicProposition;
import formulas.ctl.CTLFormula;
import formulas.ctl.ExistsAlways;
import formulas.ctl.ExistsEventually;
import formulas.ctl.ExistsNext;
import formulas.ctl.ExistsUntil;
import formulas.ctl.False;
import formulas.ctl.ForAllAlways;
import formulas.ctl.ForAllEventually;
import formulas.ctl.ForAllNext;
import formulas.ctl.Iff;
import formulas.ctl.Implies;
import formulas.ctl.Not;
import formulas.ctl.Or;
import formulas.ctl.True;
import model.ctl.CTLModelChecker;

/**
 * Tests the ModelChecker class.
 * 
 * @author Franck van Breugel
 */
public class ModelCheckerTest {
	/**
	 * Number of times each test is run.
	 */
	private static final int CASES = 1000;

	/**
	 * Tests the formula true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testTrue() {
		Formula formula = new True();
		TransitionSystem system = new TransitionSystem();
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates());
		CTLModelChecker model = new CTLModelChecker(system);
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
		Formula formula = new False();
		TransitionSystem system = new TransitionSystem();
		BitSet expected = new BitSet();
		CTLModelChecker model = new CTLModelChecker(system);
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
	public void testAtomicProposition() {
		String name = "C.f";
		Formula formula = new AtomicProposition(name);
		TransitionSystem system = new TransitionSystem(formula.getAtomicPropositions());
		BitSet expected = new BitSet();
		int index = system.getIndices().get(name);
		if (system.getLabelling().containsKey(index)) {
			expected = system.getLabelling().get(index);
		} 
		CTLModelChecker model = new CTLModelChecker(system);
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
		TransitionSystem system = new TransitionSystem(formula.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
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
		Set<String> atomicPropositions = left.getAtomicPropositions();
		atomicPropositions.addAll(right.getAtomicPropositions());
		TransitionSystem system = new TransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(system);
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
		Set<String> atomicPropositions = left.getAtomicPropositions();
		atomicPropositions.addAll(right.getAtomicPropositions());
		TransitionSystem system = new TransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		BitSet expected = expectedLeft;
		expected.or(expectedRight);
		Formula or = new Or(left, right);
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
		Set<String> atomicPropositions = left.getAtomicPropositions();
		atomicPropositions.addAll(right.getAtomicPropositions());
		TransitionSystem system = new TransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		expectedLeft.flip(0, system.getNumberOfStates());
		BitSet expected = expectedLeft;
		expected.or(expectedRight);
		Formula implies = new Implies(left, right);
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
		Set<String> atomicPropositions = left.getAtomicPropositions();
		atomicPropositions.addAll(right.getAtomicPropositions());
		TransitionSystem system = new TransitionSystem(atomicPropositions);
		CTLModelChecker model = new CTLModelChecker(system);
		Result resultLeft = model.check(left);
		Result resultRight = model.check(right);
		BitSet expectedLeft = resultLeft.getLower();
		BitSet expectedRight = resultRight.getLower();
		assertEquals(expectedLeft, resultLeft.getLower(), left.toString() + "\n" + system.toString());
		assertEquals(expectedRight, resultRight.getLower(), right.toString() + "\n" + system.toString());
		
		// p <-> q === (!p or q) and (!q or p)
		BitSet p = resultLeft.getLower();
		BitSet q = resultRight.getLower();
		
		// !p
		BitSet notP = (BitSet) p.clone();
		notP.flip(0, system.getNumberOfStates());
		
		// !q
		BitSet notQ = (BitSet) q.clone();
		notQ.flip(0, system.getNumberOfStates());
		
		// (!p or q)
		BitSet notPorQ = (BitSet) notP.clone();
		notPorQ.or(q);
		
		// (!q or p)
		BitSet notQorP = (BitSet) notQ.clone();
		notQorP.or(p);
		
		// (!p or q) and (!q or p)
		BitSet expected = (BitSet) notPorQ.clone();
		expected.and(notQorP);
		
		Formula iff = new Iff(left, right);
		Result result = model.check(iff);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, iff.toString() + "\n" + system.toString());

		//old
		BitSet positive = expectedLeft;
		positive.and(expectedRight);
		expectedLeft = resultLeft.getUpper();
		expectedLeft.flip(0, system.getNumberOfStates());
		expectedRight = resultRight.getUpper();
		expectedRight.flip(0, system.getNumberOfStates());
		BitSet negative = expectedLeft;
		negative.and(expectedRight);
		expected = positive;
		expected.or(negative);
		//
		
		
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
	 * 
	 * TODO does not handle special cases
	 */
	@RepeatedTest(CASES)
	public void testExistsNextTrue() {
		ExistsNext existsNext = new ExistsNext(new True());
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
		
		// Test Lower
		BitSet expectedLower = new BitSet();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			 if (system.getSuccessors().containsKey(state)) {
				 expectedLower.set(state);
			 }
		}
		model = new CTLModelChecker(system);
		Result result = model.check(existsNext);
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, existsNext.toString() + "\n" + system.toString());

		BitSet expectedUpper = (BitSet) expectedLower.clone();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				expectedUpper.set(state);
			}
		}
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, existsNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EX false for a random system. 
	 * 
	 * TODO not considering special case for false
	 */
	@RepeatedTest(CASES)
	public void testExistsNextFalse() {
		ExistsNext existsNext = new ExistsNext(new False());
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
		
		// Test Lower
		BitSet expectedLower = new BitSet();
		Result result = model.check(existsNext);
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, existsNext.toString() + "\n" + system.toString());

		// Test Upper
		BitSet expectedUpper = (BitSet) expectedLower.clone();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				expectedUpper.set(state);
			}
		}
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, existsNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EX atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsNextAtomicProposition() {
		String name = "C.f";
		ExistsNext existsNext = new ExistsNext(new AtomicProposition(name));
		TransitionSystem system = new TransitionSystem(existsNext.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
		BitSet expectedLower = new BitSet();
		int index = system.getIndices().get(name);
		if (system.getLabelling().containsKey(index)) {
			for (int state = 0; state < system.getNumberOfStates(); state++) {
					boolean holds = system.getSuccessors().containsKey(state) // state has successors
							&& system.getLabelling().containsKey(index) // atomic proposition exists
							&& system.getSuccessors().get(state).intersects(system.getLabelling().get(index)); // one of the successors is labelled with the atomic proposition
					expectedLower.set(state, holds);
			}
		}
		Result result = model.check(existsNext);
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, existsNext.toString() + "\n" + system.toString());

		BitSet expectedUpper = (BitSet) expectedLower.clone();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				expectedUpper.set(state);
			}
		}
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, existsNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AX true for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllNextTrue() {
		ForAllNext forAllNext = new ForAllNext(new True());
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
		BitSet expectedLower = new BitSet();
		
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (!system.getPartial().get(state)) {
				if (system.getSuccessors().containsKey(state)) {
					expectedLower.set(state);
				}
			}
		}
		Result result = model.check(forAllNext);
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, forAllNext.toString() + "\n" + system.toString());

		BitSet expectedUpper = (BitSet) expectedLower.clone();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				expectedUpper.set(state);
			}
		}
		
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, forAllNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AX false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllNextFalse() {
		ForAllNext forAllNext = new ForAllNext(new False());
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(forAllNext);
		BitSet expectedLower = new BitSet();
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, forAllNext.toString() + "\n" + system.toString());

		BitSet expectedUpper = (BitSet) expectedLower.clone();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				expectedUpper.set(state);
			}
		}
		
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, forAllNext.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula AX atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testForAllNextAtomicProposition() {
		String name = "C.f";
		ForAllNext forAllNext = new ForAllNext(new AtomicProposition(name));
		TransitionSystem system = new TransitionSystem(forAllNext.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
		int index = system.getIndices().get(name);
		BitSet expected = new BitSet();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (!system.getPartial().get(state)) {
				if (system.getSuccessors().containsKey(state) ) {
					if (system.getLabelling().containsKey(index)) {
						BitSet post = system.getSuccessors().get(state);
						if (subset(post, system.getLabelling().get(index))) {
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
		
		BitSet expectedLower = new BitSet();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (!system.getPartial().get(state)) {
				expectedLower.set(state);
			} else {
				if (system.getSuccessors().containsKey(state)) {
					expectedLower.set(state);
				}
			}
		}
		Result result = model.check(existsAlways);
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, existsAlways.toString() + "\n" + system.toString());

		BitSet expectedUpper = new BitSet();
		expectedUpper.set(0, system.getNumberOfStates());
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, existsAlways.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula EG false for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsAlwaysFalse() {
		ExistsAlways existsAlways = new ExistsAlways(new False());
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
	public void testExistsAlwaysAtomicProposition() {
		String name = "C.f";
		ExistsAlways existsAlways = new ExistsAlways(new AtomicProposition(name));
		TransitionSystem system = new TransitionSystem(existsAlways.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(existsAlways);
		int index = system.getIndices().get(name);
		if (system.getLabelling().containsKey(index)) {
			BitSet actual = result.getLower();
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				assertTrue(system.getLabelling().get(index).get(state), "state " + state + " is not labelled C.f\n" + system.toString());
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(post.intersects(actual), "state " + state + " has transitions but no transition to a state satisfying EG C.f\n" + system.toString());
				}
			}
			actual.flip(0, system.getNumberOfStates());
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(!system.getLabelling().get(index).get(state) || subset(post, actual), "state " + state + " is not labelled C.f\n" + system.toString());
				}
			}

			actual = result.getUpper();
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				assertTrue(system.getLabelling().get(index).get(state), "state " + state + " is not labelled C.f\n" + system.toString());
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(system.getPartial().get(state) || post.intersects(actual), "state " + state + " is fully explored, has transitions but no transition to a state satisfying EG C.f\n" + system.toString());
				}
			}
			actual.flip(0, system.getNumberOfStates());
			for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
				if (system.getSuccessors().containsKey(state)) {
					BitSet post = system.getSuccessors().get(state);
					assertTrue(!system.getLabelling().get(index).get(state) || subset(post, actual), "state " + state + " is not labelled C.f\n" + system.toString());
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
	public void testForAllAlwaysAtomicProposition() {
		String name = "C.f";
		ForAllAlways forAllAlways = new ForAllAlways(new AtomicProposition(name));
		TransitionSystem system = new TransitionSystem(forAllAlways.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(forAllAlways);
		int index = system.getIndices().get(name);
		
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
				assertTrue(system.getLabelling().get(index).get(state), "state " + state + " is not labelled C.f\n" + system.toString());
				assertFalse(system.getPartial().get(state), "state " + state + " is partially explored\n" + system);
				assertTrue(subset(post, lower), "post(" + state + ") is not a subset of the lower approximation\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(index).get(state)
						|| !system.getPartial().get(state)
						|| !subset(post, lower));
			}
			
			if (upper.get(state)) {
				assertTrue(system.getLabelling().get(index).get(state), "state " + state + " is not labelled C.f\n" + system.toString());
				assertTrue(subset(post, upper), "post(" + state + ") is not a subset of the upper approximation\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(index).get(state)
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
	public void testExistsEventuallyAtomicProposition() {
		String name = "C.f";
		ExistsEventually existsEventually = new ExistsEventually(new AtomicProposition(name));
		TransitionSystem system = new TransitionSystem(existsEventually.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(existsEventually);
		int index = system.getIndices().get(name);
		
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
				assertTrue(system.getLabelling().get(index).get(state)
						|| post.intersects(lower));
			} else {
				assertTrue(!system.getLabelling().get(index).get(state)
						&& !post.intersects(lower));
			}
			
			if (upper.get(state)) {
				assertTrue(system.getLabelling().get(index).get(state)
						|| system.getPartial().get(state)
						|| post.intersects(upper));
			} else {
				assertTrue(!system.getLabelling().get(index).get(state)
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
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
	public void testForAllEventuallyAtomicProposition() {
		String name = "C.f";
		ForAllEventually forAllEventually = new ForAllEventually(new AtomicProposition(name));
		TransitionSystem system = new TransitionSystem(forAllEventually.getAtomicPropositions());
		Map<String, String> jniMapping = new HashMap<>();
		for (String ap : forAllEventually.getAtomicPropositions()) {
			jniMapping.put(ap, ap);
		}
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(forAllEventually);
		int index = system.getIndices().get(name);
		
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
				assertTrue(system.getLabelling().get(index).get(state)
						|| (!system.getPartial().get(state) && !post.isEmpty() && subset(post, lower)), "state " + state + " in " + lower + "\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(index).get(state)
						&& (system.getPartial().get(state) || post.isEmpty() || !subset(post, lower)), "state " + state + " not in " + lower + "\n" + system);
			}
			
			if (upper.get(state)) {
				assertTrue(system.getLabelling().get(index).get(state)
						|| (!(!system.getPartial().get(state) && post.isEmpty()) && subset(post, upper)), "state " + state + " in " + upper + "\n" + system);
			} else {
				assertTrue(!system.getLabelling().get(index).get(state)
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
		TransitionSystem system = new TransitionSystem(left.getAtomicPropositions());
		BitSet expected = new BitSet();
		expected.set(0, system.getNumberOfStates());
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(existsUntil);
		BitSet actual = result.getLower();
		assertEquals(expected, actual, existsUntil.toString() + "\n" + system.toString());

		actual = result.getUpper();
		assertEquals(expected, actual, existsUntil.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula f EU false, where f is a random formula, for a random system. 
	 * 
	 * TODO doesn't handle special case -> should always be false (algo change needed)
	 */
	@RepeatedTest(CASES)
	public void testExistsUntilFalse() {
		CTLFormula left = CTLFormula.random();
		ExistsUntil existsUntil = new ExistsUntil(left, new False());
		TransitionSystem system = new TransitionSystem();
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(existsUntil);
		
		// Test Lower
		BitSet expectedLower = new BitSet(system.getNumberOfStates());
		BitSet actual = result.getLower();
		assertEquals(expectedLower, actual, existsUntil.toString() + "\n" + system.toString());

		// Test Upper
		BitSet expectedUpper = new BitSet(system.getNumberOfStates());
		Result leftResult = model.check(left);
		BitSet leftLower = leftResult.getLower();
		for (int state = 0; state < system.getNumberOfStates(); state++) {
			if (system.getPartial().get(state)) {
				if (leftLower.get(state)) { // if this state satisfies left
					expectedUpper.set(state);
				}
			}
		}
		actual = result.getUpper();
		assertEquals(expectedUpper, actual, existsUntil.toString() + "\n" + system.toString());
	}

	/**
	 * Tests the formula atomic proposition EU atomic proposition for a random system. 
	 */
	@RepeatedTest(CASES)
	public void testExistsUntilAtomicProposition() {
		String left = "C.l";
		String right = "C.r";
		ExistsUntil existsUntil = new ExistsUntil(new AtomicProposition(left), new AtomicProposition(right));
		TransitionSystem system = new TransitionSystem(existsUntil.getAtomicPropositions());
		int leftIndex = system.getIndices().get(left);
		int rightIndex = system.getIndices().get(right);
		CTLModelChecker model = new CTLModelChecker(system);
		Result result = model.check(existsUntil);
		BitSet actual = result.getLower();
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(rightIndex) && system.getLabelling().get(rightIndex).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(leftIndex) && system.getLabelling().get(leftIndex).get(state);
			BitSet post = system.getSuccessors().get(state);
			assertTrue(satisfiesRight || (satisfiesLeft && post.intersects(actual)), "state " + state + " does not satisy C.left EU C.right\n" + system.toString());
		}
		actual.flip(0, system.getNumberOfStates());
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(rightIndex) && system.getLabelling().get(rightIndex).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(leftIndex) && system.getLabelling().get(leftIndex).get(state);
			if (system.getSuccessors().containsKey(state)) {
				BitSet post = system.getSuccessors().get(state);
				assertTrue((!satisfiesRight && !satisfiesLeft) || (!satisfiesRight && satisfiesLeft && subset(post, actual)),"state " + state + " satisfies C.left EU C.right\n" + system.toString());
			}
		}

		actual = result.getUpper();
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(rightIndex) && system.getLabelling().get(rightIndex).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(leftIndex) && system.getLabelling().get(leftIndex).get(state);
			BitSet post = system.getSuccessors().get(state);
			assertTrue(satisfiesRight || (satisfiesLeft && (system.getPartial().get(state) || post.intersects(actual))), "state " + state + " does not satisy C.left EU C.right\n" + system.toString());
		}
		actual.flip(0, system.getNumberOfStates());
		for (int state = actual.nextSetBit(0); state != -1; state = actual.nextSetBit(state + 1)) {
			boolean satisfiesRight = system.getLabelling().containsKey(rightIndex) && system.getLabelling().get(rightIndex).get(state);
			boolean satisfiesLeft = system.getLabelling().containsKey(leftIndex) && system.getLabelling().get(leftIndex).get(state);
			if (system.getSuccessors().containsKey(state)) {
				BitSet post = system.getSuccessors().get(state);
				assertTrue((!satisfiesRight && !satisfiesLeft) || (!satisfiesRight && satisfiesLeft && subset(post, actual)),"state " + state + " satisfies C.left EU C.right\n" + system.toString());
			}
		}
	}

	/**
	 * Tests that the lower approximation is a subset of the upper approximation for a random formula and a random system. 
	 */
	@RepeatedTest(CASES)
	public void testUpperSubsetLower() {
		CTLFormula formula = CTLFormula.random();
		TransitionSystem system = new TransitionSystem(formula.getAtomicPropositions());
		CTLModelChecker model = new CTLModelChecker(system);
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
