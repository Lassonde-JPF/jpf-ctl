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
import formulas.ctl.CTLFormula;
import formulas.ctl.ExistsUntil;
import formulas.ctl.ForAllUntil;
import formulas.ctl.Iff;
import formulas.ctl.Implies;
import formulas.ctl.Or;

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
			String formula = first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parse(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = this.generator.visit(tree);
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
			String formula = first.toString() + " && " + second.toString() + " && " + third.toString();
			ParseTree tree = parse(formula);
			CTLFormula actual = this.generator.visit(tree);
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
			String formula = first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			ParseTree tree = parse(formula);
			CTLFormula actual = this.generator.visit(tree);
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
			String formula = first.toString() + " -> " + second.toString() + " -> " + third.toString();
			ParseTree tree = parse(formula);
			CTLFormula actual = this.generator.visit(tree);
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
			String formula = first.toString() + " EU " + second.toString() + " EU " + third.toString();
			ParseTree tree = parse(formula);
			CTLFormula actual = this.generator.visit(tree);
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
			String formula = first.toString() + " AU " + second.toString() + " AU " + third.toString();
			ParseTree tree = parse(formula);
			CTLFormula actual = this.generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}
}
