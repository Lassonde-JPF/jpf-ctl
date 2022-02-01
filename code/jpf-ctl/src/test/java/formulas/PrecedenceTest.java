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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new Or(first, new And(second, third));
			// create its string representation without parentheses
			String formula = first.toString() + " || " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parse(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			// combine the three (in another way)
			expected = new Or(new And(first, second), third);
			// create its string representation without parentheses
			formula = first.toString() + " && " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			tree = parse(formula);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Implies(new And(first, second), third);
			String formula = first.toString() + " && " + second.toString() + " -> " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Implies(first, new And(second, third));
			formula = first.toString() + " -> " + second.toString() + " && " + third.toString();
			tree = parse(formula);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Iff(new And(first, second), third);
			String formula = first.toString() + " && " + second.toString() + " <-> " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(first, new And(second, third));
			formula = first.toString() + " <-> " + second.toString() + " && " + third.toString();
			tree = parse(formula);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new Not(first), second);
			String formula = "! " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AX operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndForAllNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new ForAllNext(first), second);
			String formula = "AX " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AG operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndForAllAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new ForAllAlways(first), second);
			String formula = "AG " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AF operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndForAllEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new ForAllEventually(first), second);
			String formula = "AF " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EX operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndExistsNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new ExistsNext(first), second);
			String formula = "EX " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EG operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndExistsAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new ExistsAlways(first), second);
			String formula = "EG " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EF operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndExistsEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new And(new ExistsEventually(first), second);
			String formula = "EF " + first.toString() + " && " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AU operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndForAllUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new And(new ForAllUntil(first, second), third);
			String formula = first.toString() + " AU " + second.toString() + " && " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new And(first, new ForAllUntil(second, third));
			formula = first.toString() + " && " + second.toString() + " AU " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EU operator has a higher precedence than the and operator.
	 */
	@Test
	public void testAndExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new And(new ExistsUntil(first, second), third);
			String formula = first.toString() + " EU " + second.toString() + " && " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new And(first, new ExistsUntil(second, third));
			formula = first.toString() + " && " + second.toString() + " EU " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Implies(first, new Or(second, third));
			String formula = first.toString() + " -> " + second.toString() + " || " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Implies(new Or(first, second), third);
			formula = first.toString() + " || " + second.toString() + " -> " + third.toString();
			tree = parse(formula);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Iff(new Implies(first, second), third);
			String formula = first.toString() + " -> " + second.toString() + " <-> " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(first, new Implies(second, third));
			formula = first.toString() + " <-> " + second.toString() + " -> " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AX operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesForAllNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new ForAllNext(first), second);
			String formula = "AX " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new Not(first), second);
			String formula = "! " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AG operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesForAllAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new ForAllAlways(first), second);
			String formula = "AG " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AF operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesForAllEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new ForAllEventually(first), second);
			String formula = "AF " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EX operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesExistsNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new ExistsNext(first), second);
			String formula = "EX " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EG operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesExistsAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new ExistsAlways(first), second);
			String formula = "EG " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EF operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesExistsEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Implies(new ExistsEventually(first), second);
			String formula = "EF " + first.toString() + " -> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AU operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesForAllUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Implies(first, new ForAllUntil(second, third));
			String formula = first.toString() + " -> " + second.toString() + " AU " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Implies(new ForAllUntil(first, second), third);
			formula = first.toString() + " AU " + second.toString() + " -> " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EU operator has a higher precedence than the implies operator.
	 */
	@Test
	public void testImpliesExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Implies(first, new ExistsUntil(second, third));
			String formula = first.toString() + " -> " + second.toString() + " EU " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Implies(new ExistsUntil(first, second), third);
			formula = first.toString() + " EU " + second.toString() + " -> " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Iff(first, new Or(second, third));
			String formula = first.toString() + " <-> " + second.toString() + " || " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(new Or(first, second), third);
			formula = first.toString() + " || " + second.toString() + " <-> " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AX operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffForAllNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Iff(new Iff(new ForAllNext(first), second), third);
			String formula = "AX " + first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Iff(new Not(first), second);
			String formula = "! " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AG operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffForAllAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Iff(new ForAllAlways(first), second);
			String formula = "AG " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AF operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffForAllEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Iff(new ForAllEventually(first), second);
			String formula = "AF " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EX operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffExistsNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Iff(new ExistsNext(first), second);
			String formula = "EX " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EG operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffExistsAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Iff(new ExistsAlways(first), second);
			String formula = "EG " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EF operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffExistsEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Iff(new ExistsEventually(first), second);
			String formula = "EF " + first.toString() + " <-> " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AU operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffForAllUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Iff(first, new ForAllUntil(second, third));
			String formula = first.toString() + " <-> " + second.toString() + " AU " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(new ForAllUntil(first, second), third);
			formula = first.toString() + " AU " + second.toString() + " <-> " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EU operator has a higher precedence than the iff operator.
	 */
	@Test
	public void testIffExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Iff(first, new ExistsUntil(second, third));
			String formula = first.toString() + " <-> " + second.toString() + " EU " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Iff(new ExistsUntil(first, second), third);
			formula = first.toString() + " EU " + second.toString() + " <-> " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
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
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new Not(first), second);
			String formula = "! " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AX operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrForAllNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new ForAllNext(first), second);
			String formula = "AX " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AG operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrForAllAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new ForAllAlways(first), second);
			String formula = "AG " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AF operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrForAllEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new ForAllEventually(first), second);
			String formula = "AF " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EX operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrExistsNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new ExistsNext(first), second);
			String formula = "EX " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EG operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrExistsAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new ExistsAlways(first), second);
			String formula = "EG " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EF operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrExistsEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new Or(new ExistsEventually(first), second);
			String formula = "EF " + first.toString() + " || " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AU operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrForAllUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Or(new ForAllUntil(first, second), third);
			String formula = first.toString() + " AU " + second.toString() + " || " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Or(first, new ForAllUntil(second, third));
			formula = first.toString() + " || " + second.toString() + " AU " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EU operator has a higher precedence than the or operator.
	 */
	@Test
	public void testOrExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new Or(new ExistsUntil(first, second), third);
			String formula = first.toString() + " EU " + second.toString() + " || " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new Or(first, new ExistsUntil(second, third));
			formula = first.toString() + " || " + second.toString() + " EU " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AU operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testForAllUntilExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			Formula expected = new ExistsUntil(first, new ForAllUntil(second, third));
			String formula = first.toString() + " EU " + second.toString() + " AU " + third.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);

			expected = new ExistsUntil(new ForAllUntil(first, second), third);
			formula = first.toString() + " AU " + second.toString() + " EU " + third.toString();
			tree = parse(formula);
			actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the not operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilNot() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new Not(first), second);
			String formula = "! " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AX operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilForAllNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new ForAllNext(first), second);
			String formula = "AX " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AG operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilForAllAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new ForAllAlways(first), second);
			String formula = "AG " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AF operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilForAllEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new ForAllEventually(first), second);
			String formula = "AF " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EX operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilExistsNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new ExistsNext(first), second);
			String formula = "EX " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EG operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilExistsAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new ExistsAlways(first), second);
			String formula = "EG " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EF operator has a higher precedence than the AU operator.
	 */
	@Test
	public void testForAllUntilExistsEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ForAllUntil(new ExistsEventually(first), second);
			String formula = "EF " + first.toString() + " AU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the not operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilNot() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new Not(first), second);
			String formula = "! " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AX operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilForAllNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new ForAllNext(first), second);
			String formula = "AX " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AG operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilForAllAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new ForAllAlways(first), second);
			String formula = "AG " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the AF operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilForAllEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new ForAllEventually(first), second);
			String formula = "AF " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EX operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilExistsNext() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new ExistsNext(first), second);
			String formula = "EX " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EG operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilExistsAlways() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new ExistsAlways(first), second);
			String formula = "EG " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the EF operator has a higher precedence than the EU operator.
	 */
	@Test
	public void testExistsUntilExistsEventually() {
		for (int c = 0; c < CASES; c++) {
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula expected = new ExistsUntil(new ExistsEventually(first), second);
			String formula = "EF " + first.toString() + " EU " + second.toString();
			ParseTree tree = parse(formula);
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}
}
