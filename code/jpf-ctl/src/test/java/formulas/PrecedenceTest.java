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

package formulas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.tree.ParseTree;

import org.junit.jupiter.api.Test;

import formulas.ctl.And;
import formulas.ctl.Iff;
import formulas.ctl.Implies;
import formulas.ctl.Not;
import formulas.ctl.Or;
import formulas.ctl.CTLFormula;

/**
 * Tests that the precedence of operators.
 *
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 * @author Parssa Khazra
 * @author Hongru Wang
 */
public class PrecedenceTest extends BaseTest {

	/**
	 * Tests that the and operator has a higher precedence than the or operator.
	 */
	@Test
	public void testAndOr() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			// combine the three
			CTLFormula expected = new Or(first, new And(second, third));
			// create its string representation without parentheses
			String CTLFormula = first.toString() + " || " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parse(CTLFormula);
			// generate an abstract syntax tree from the parse tree
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			// combine the three (in another way)
			expected = new Or(new And(first, second), third);
			// create its string representation without parentheses
			CTLFormula = first.toString() + " && " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			tree = parse(CTLFormula);
			// generate an abstract syntax tree from the parse tree
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the and operator has a higher precedence than the implies
	 * operator.
	 */
	@Test
	public void testAndImplies() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Implies(new And(first, second), third);
			String CTLFormula = first.toString() + " && " + second.toString() + " -> " + third.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Implies(first, new And(second, third));
			CTLFormula = first.toString() + " -> " + second.toString() + " && " + third.toString();
			tree = parse(CTLFormula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the and operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testAndIff() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Iff(new And(first, second), third);
			String CTLFormula = first.toString() + " && " + second.toString() + " <-> " + third.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(first, new And(second, third));
			CTLFormula = first.toString() + " <-> " + second.toString() + " && " + third.toString();
			tree = parse(CTLFormula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the not operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndNot() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula expected = new And(new Not(first), second);
			String CTLFormula = "! " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the or operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesOr() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Implies(first, new Or(second, third));
			String CTLFormula = first.toString() + " -> " + second.toString() + " || " + third.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Implies(new Or(first, second), third);
			CTLFormula = first.toString() + " || " + second.toString() + " -> " + third.toString();
			tree = parse(CTLFormula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the implies operator has a higher precedence than the iff
	 * operator.
	 */
	@Test
	public void testImpliesIff() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Iff(new Implies(first, second), third);
			String CTLFormula = first.toString() + " -> " + second.toString() + " <-> " + third.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(first, new Implies(second, third));
			CTLFormula = first.toString() + " <-> " + second.toString() + " -> " + third.toString();
			tree = parse(CTLFormula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the not operator has a higher precedence than the implies
	 * operator.
	 */
	@Test
	public void testImpliesNot() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula expected = new Implies(new Not(first), second);
			String CTLFormula = "! " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the or operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffOr() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula third = CTLFormula.random();
			CTLFormula expected = new Iff(first, new Or(second, third));
			String CTLFormula = first.toString() + " <-> " + second.toString() + " || " + third.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(new Or(first, second), third);
			CTLFormula = first.toString() + " || " + second.toString() + " <-> " + third.toString();
			tree = parse(CTLFormula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the not operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffNot() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula expected = new Iff(new Not(first), second);
			String CTLFormula = "! " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the not operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrNot() {
		for (int c = 0; c < CASES; c++) {
			CTLFormula first = CTLFormula.random();
			CTLFormula second = CTLFormula.random();
			CTLFormula expected = new Or(new Not(first), second);
			String CTLFormula = "! " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(CTLFormula);
			CTLFormula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}
}
