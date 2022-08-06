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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests that the binary operators are left or right associative.
 * 
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 * @author Parssa Khazra
 * @author Hongru Wang
 */
public class AssociativityTest {

	/**
	 * Number of random cases that are considered in each test.
	 */
	private static final int CASES = 1000;

	/**
	 * Tests that the or operator is left associative.
	 */
	@Test
	public void testOr() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			// combine the three
			CTLFormula expected = new Or(new Or(first, second), third);
			// create its string representation without parentheses
			String representation = first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain abstract syntax tree
			CTLFormulaParser parser = new CTLFormulaParser();
			CTLFormula actual = parser.parse(representation);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the and operator is left associative.
	 */
	@Test
	public void testAnd() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new And(new And(first, second), third);
			String representation = first.toString() + " && " + second.toString() + " && " + third.toString();
			CTLFormulaParser parser = new CTLFormulaParser();
			CTLFormula actual = parser.parse(representation);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the equivalence (iff) operator is left associative.
	 */
	@Test
	public void testIff() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Iff(new Iff(first, second), third);
			String representation = first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			CTLFormulaParser parser = new CTLFormulaParser();
			CTLFormula actual = parser.parse(representation);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the implies operator is right associative.
	 */
	@Test
	public void testImplies() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Implies(first,new Implies(second, third));
			String representation = first.toString() + " -> " + second.toString() + " -> " + third.toString();
			CTLFormulaParser parser = new CTLFormulaParser();
			CTLFormula actual = parser.parse(representation);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}


	/**
	 * Tests that the EA operator is right associative.
	 */
	@Test
	public void testExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new ExistsUntil(first,new ExistsUntil(second, third));
			String representation = first.toString() + " EU " + second.toString() + " EU " + third.toString();
			CTLFormulaParser parser = new CTLFormulaParser();
			CTLFormula actual = parser.parse(representation);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}


	/**
	 * Tests that the AU operator is right associative.
	 */
	@Test
	public void testForAllUntil() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new ForAllUntil(first,new ForAllUntil(second, third));
			String representation = first.toString() + " AU " + second.toString() + " AU " + third.toString();
			CTLFormulaParser parser = new CTLFormulaParser();
			CTLFormula actual = parser.parse(representation);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}
}
