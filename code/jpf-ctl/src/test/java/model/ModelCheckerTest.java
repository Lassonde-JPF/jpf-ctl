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
import formulas.ctl.False;
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
	private static final int CASES = 10000;

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
