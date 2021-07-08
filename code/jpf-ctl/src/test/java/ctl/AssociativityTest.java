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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import error.CTLError;

/**
 * Tests that the binary operators are left or right associative.
 * 
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 *
 */

public class AssociativityTest {

	private Generator generator;

	private static final int CASES = 1000;

	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}

	/**
	 * Tests that the or operator is left associative.
	 */
	@Test
	public void testOr() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new Or(new Or(first, second), third);
			// create its string representation without parentheses
			String formula = first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the And operator is left associative.
	 */
	@Test
	public void testAnd() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new And(new And(first, second), third);
			// create its string representation without parentheses
			String formula = first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Tests that the equivalence(iff) operator is left associative.
	 */
	@Test
	public void testIff() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new Iff(new Iff(first, second), third);
			// create its string representation without parentheses
			String formula = first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
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
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new Implies(first,new Implies(second, third));
			// create its string representation without parentheses
			String formula = first.toString() + " -> " + second.toString() + " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	
	/**
	 * Tests that the existuntil operator is right associative.
	 */
	@Test
	public void testExistsUntil() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new ExistsUntil(first,new ExistsUntil(second, third));
			// create its string representation without parentheses
			String formula = first.toString() + " EU " + second.toString() + " EU " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}
	
	
	/**
	 * Tests that the existuntil operator is right associative.
	 */
	@Test
	public void testForAllUntil() {
		for (int c = 0; c < CASES; c++) {
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new ForAllUntil(first,new ForAllUntil(second, third));
			// create its string representation without parentheses
			String formula = first.toString() + " AU " + second.toString() + " AU " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}
	
	/**
	 * Translates a syntactically correct CTL formula from its String form to a
	 * ParseTree.
	 * 
	 * @param ctlFormula
	 * @return The ParseTree representation of the given CTL formula
	 */
	private ParseTree parseCtl(String formula) 
	{
		CharStream input = CharStreams.fromString(formula);
		CTLError error = new CTLError();
		input = error.errorCheckAndRecover(input);

		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		
		ParseTree tree = parser.formula();

		return tree;
	}
}
