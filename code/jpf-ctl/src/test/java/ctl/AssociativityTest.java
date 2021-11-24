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

package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.RepeatedTest;

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
public class AssociativityTest extends BaseTest {

	/**
	 * Tests that the or operator is left associative.
	 */
	@RepeatedTest(TIMES)
	public void testOr() {
		// generate three random abstract syntax trees
		Formula first = Formula.random();
		Formula second = Formula.random();
		Formula third = Formula.random();
		// combine the three
		Formula expected = new Or(new Or(first, second), third);
		// create its string representation without parentheses
		String formula = first + " || " + second + " || " + third;
		// obtain the abstract syntax tree
		Formula actual = parse(formula);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	/**
	 * Tests that the and operator is left associative.
	 */
	@RepeatedTest(TIMES)
	public void testAnd() {
		Formula first = Formula.random();
		Formula second = Formula.random();
		Formula third = Formula.random();
		Formula expected = new And(new And(first, second), third);
		String formula = first + " && " + second + " && " + third;
		Formula actual = parse(formula);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	/**
	 * Tests that the equivalence (iff) operator is left associative.
	 */
	@RepeatedTest(TIMES)
	public void testIff() {
		Formula first = Formula.random();
		Formula second = Formula.random();
		Formula third = Formula.random();
		Formula expected = new Iff(new Iff(first, second), third);
		String formula = first + " <-> " + second + " <-> " + third;
		Formula actual = parse(formula);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	/**
	 * Tests that the implies operator is right associative.
	 */
	@RepeatedTest(TIMES)
	public void testImplies() {
		Formula first = Formula.random();
		Formula second = Formula.random();
		Formula third = Formula.random();
		Formula expected = new Implies(first,new Implies(second, third));
		String formula = first + " -> " + second + " -> " + third;
		Formula actual = parse(formula);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	/**
	 * Tests that the EA operator is right associative.
	 */
	@RepeatedTest(TIMES)
	public void testExistsUntil() {
		Formula first = Formula.random();
		Formula second = Formula.random();
		Formula third = Formula.random();
		Formula expected = new ExistsUntil(first,new ExistsUntil(second, third));
		String formula = first + " EU " + second + " EU " + third;
		Formula actual = parse(formula);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	/**
	 * Tests that the AU operator is right associative.
	 */
	@RepeatedTest(TIMES)
	public void testForAllUntil() {
		Formula first = Formula.random();
		Formula second = Formula.random();
		Formula third = Formula.random();
		Formula expected = new ForAllUntil(first,new ForAllUntil(second, third));
		String formula = first + " AU " + second + " AU " + third;
		Formula actual = parse(formula);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
