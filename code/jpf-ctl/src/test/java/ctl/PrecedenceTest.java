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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import errors.DialogListener;
import parser.CTLLexer;
import parser.CTLParser;

/**
 * Tests that the precedence of operators.
 * 
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 *
 */
public class PrecedenceTest {


	private Generator generator;

	private static final int CASES = 1000;
	/**
	 * Tests that the and operator has a higher precedence than the or operator.
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}

	@Test
	public void testAndOr() 
	{ 
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			Formula expected = new Or(first, new And(second, third));
			// create its string representation without parentheses 
			String formula = first.toString() + " || " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndAnd() 
	{ 
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			// a && b && c = (a&&b) && c
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

	@Test
	public void testAndImplies()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a && s -> t := (a && s) -> t
			// combine the three
			Formula expected = new Implies(new And(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " && " + second.toString() + " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndIff()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a && s <-> t := (a && s) <-> t
			// combine the three
			Formula expected = new Iff(new And(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " && " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	// the tests for this and the other unary operators should be the same
	public void testAndNot()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			//	Formula third = Formula.random();

			// ! A && B = (!A) && B
			// combine the three
			Formula expected = new And(new Not(first), second);

			// create its string representation without parentheses 

			String formula = "! "+first.toString() + " && " + second.toString();// + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndForAllNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new And(new And(new ForAllNext(first), second), third);
			// create its string representation without parentheses 

			String formula = "AX "+first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndForAllAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new And(new And(new ForAllAlways(first), second), third);
			// create its string representation without parentheses 

			String formula = "AG "+first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndForAllEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AF A && B && C =  ((AF A) && B ) && C)
			// combine the three
			Formula expected = new And(new And(new ForAllEventually(first), second), third);
			// create its string representation without parentheses 

			String formula = "AF "+first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndExistsNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// EX A && B && C =  ((EX A) && B ) && C)
			// combine the three
			Formula expected = new And(new And(new ExistsNext(first), second), third);
			// create its string representation without parentheses 

			String formula = "EX "+first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndExistsAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// EG A && B && C =  ((EG A) && B ) && C)
			// combine the three
			Formula expected = new And(new And(new ExistsAlways(first), second), third);
			// create its string representation without parentheses 

			String formula = "EG "+first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndExistsEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// EF A && B && C =  ((EF A) && B ) && C)
			// combine the three
			Formula expected = new And(new And(new ExistsEventually(first), second), third);
			// create its string representation without parentheses 

			String formula = "EF "+first.toString() + " && " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndForAllUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			//  A AU B && C =  ((A AU B ) && C)
			// combine the three
			Formula expected = new And(new ForAllUntil(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " AU " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testAndExistsUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			//  A AU B && C =  ((A AU B ) && C)
			// combine the three
			Formula expected = new And(new ExistsUntil(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " EU " + second.toString() + " && " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	// implies tests
	@Test
	public void testImpliesImplies()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// f-> (s->t)
			// combine the three
			Formula expected = new Implies(first, new Implies(second, third));
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

	@Test
	public void testImpliesOr()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// f -> s || t :=  f -> (s || t)
			// combine the three
			Formula expected = new Implies(first, new Or(second, third));
			// create its string representation without parentheses 

			String formula = first.toString() + " -> " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}


	@Test
	public void testImpliesIFF()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// f -> s <-> t :=  (f -> s) <-> s
			// combine the three
			Formula expected = new Iff(new Implies(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " -> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesForAllNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();



			// combine the three
			Formula expected = new Implies( new ForAllNext(first), second);
			// create its string representation without parentheses 

			String formula = "AX "+first.toString() + " -> " + second.toString(); //+ " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesNot()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a -> b  = ((!a) -> b) 
			// combine the three
			Formula expected = new Implies(new Not(first), second);
			// create its string representation without parentheses 
			//System.out.println(expected.toString());

			String formula = "! "+first.toString() + " -> " + second.toString(); //+ " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesForAllAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();


			// combine the three
			Formula expected = new Implies(new ForAllAlways(first), second);
			// create its string representation without parentheses 

			String formula = "AG "+first.toString() + " -> " + second.toString(); //+ " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesForAllEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a -> b -> c = ((!a) -> b) -> c
			// combine the three
			Formula expected = new Implies( new ForAllEventually(first), second);
			// create its string representation without parentheses 

			String formula = "AF "+first.toString() + " -> " + second.toString();// + " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesExistsNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();

			// combine the three
			Formula expected = new Implies( new ExistsNext(first), second);
			// create its string representation without parentheses 

			String formula = "EX "+first.toString() + " -> " + second.toString();// + " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesExistsAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			//	Formula third = Formula.random();


			// combine the three
			Formula expected = new Implies(new ExistsAlways(first), second);
			// create its string representation without parentheses 

			String formula = "EG "+first.toString() + " -> " + second.toString();// + " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesExistsEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();

			// ! a -> b -> c = ((!a) -> b) -> c
			// combine the three
			Formula expected = new Implies( new ExistsEventually(first), second);
			// create its string representation without parentheses 

			String formula = "EF "+first.toString() + " -> " + second.toString(); //+ " -> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesForAllUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			//  a -> b AU c = a -> (b AU c)
			// combine the three
			Formula expected = new Implies(first, new ForAllUntil(second, third));
			// create its string representation without parentheses 

			String formula = first.toString() + " -> " + second.toString() + " AU " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testImpliesExistsUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			//  a -> b AU c = a -> (b AU c)
			// combine the three
			Formula expected = new Implies(first, new ExistsUntil(second, third));
			// create its string representation without parentheses 

			String formula = first.toString() + " -> " + second.toString() + " EU " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	// IFF
	@Test
	public void testIffIff()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a <-> b <-> c = (a <-> b) <-> c
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

	@Test
	public void testIffOr()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// f <-> s || t :=  f <-> (s || t)
			// combine the three
			Formula expected = new Iff(first, new Or(second, third));
			// create its string representation without parentheses 

			String formula = first.toString() + " <-> " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}




	@Test
	public void testIffForAllNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX a <-> b <-> c = ((AX a) <-> b) < -> c
			// combine the three
			Formula expected = new Iff(new Iff( new ForAllNext(first), second), third);
			// create its string representation without parentheses 

			String formula = "AX "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffNot()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// ! a -> b -> c = ((!a) -> b) -> c
			// combine the three
			Formula expected = new Iff(new Iff( new Not(first), second), third);
			// create its string representation without parentheses 

			String formula = "! "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffForAllAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// ! a <-> b -> c = ((!a) <-> b) -> c
			// combine the three
			Formula expected = new Iff(new Iff( new ForAllAlways(first), second), third);
			// create its string representation without parentheses 

			String formula = "AG "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffForAllEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// ! a -> b -> c = ((!a) -> b) -> c
			// combine the three
			Formula expected = new Iff(new Iff( new ForAllEventually(first), second), third);
			// create its string representation without parentheses 

			String formula = "AF "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffExistsNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// ! a <-> b <-> c = ((!a) <-> b) <-> c
			// combine the three
			Formula expected = new Iff(new Iff( new ExistsNext(first), second), third);
			// create its string representation without parentheses 

			String formula = "EX "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffExistsAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// ! a -> b -> c = ((!a) -> b) -> c
			// combine the three
			Formula expected = new Iff(new Iff( new ExistsAlways(first), second), third);
			// create its string representation without parentheses 

			String formula = "EG "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffExistsEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// ! a -> b -> c = ((!a) -> b) -> c
			// combine the three
			Formula expected = new Iff(new Iff( new ExistsEventually(first), second), third);
			// create its string representation without parentheses 

			String formula = "EF "+first.toString() + " <-> " + second.toString() + " <-> " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffForAllUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			//  a <-> b AU c = a <-> (b AU c)
			// combine the three
			Formula expected = new Iff(first, new ForAllUntil(second, third));
			// create its string representation without parentheses 

			String formula = first.toString() + " <-> " + second.toString() + " AU " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testIffExistsUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			//  a -> b AU c = a -> (b AU c)
			// combine the three
			Formula expected = new Iff(first, new ExistsUntil(second, third));
			// create its string representation without parentheses 

			String formula = first.toString() + " <-> " + second.toString() + " EU " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	//OR tests

	@Test
	public void testOrOr() 
	{ 
		for (int c = 0; c < CASES; c++)
		{
			// generate three random abstract syntax trees
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();
			// combine the three
			// a || b || c = (a||b) || c
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

	@Test
	public void testOrNot()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
		
			// ! A || B =  ((!A) || B )
			// combine the three
			Formula expected = new Or(new Not(first), second);
			// create its string representation without parentheses 

			String formula = "! "+first.toString()  +" || " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}	


	@Test
	public void testOrForAllNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new Or(new Or(new ForAllNext(first), second), third);
			// create its string representation without parentheses 

			String formula = "AX "+first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testOrForAllAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new Or(new Or(new ForAllAlways(first), second), third);
			// create its string representation without parentheses 

			String formula = "AG "+first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testOrForAllEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new Or(new Or(new ForAllEventually(first), second), third);
			// create its string representation without parentheses 

			String formula = "AF "+first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}


	@Test
	public void testOrExistsNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new Or(new Or(new ExistsNext(first), second), third);
			// create its string representation without parentheses 

			String formula = "EX "+first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testOrExistsAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new Or(new Or(new ExistsAlways(first), second), third);
			// create its string representation without parentheses 

			String formula = "EG "+first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}


	@Test
	public void testOrExistsEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// AX A && B && C =  ((AX A) && B ) && C)
			// combine the three
			Formula expected = new Or(new Or(new ExistsEventually(first), second), third);
			// create its string representation without parentheses 

			String formula = "EF "+first.toString() + " || " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testOrForAllUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a AU b || c = (a AU b) || c
			// combine the three
			Formula expected = new Or(new ForAllUntil(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " AU " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testOrExistsUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a EU b || c = (a EU b) || c
			// combine the three
			Formula expected = new Or(new ExistsUntil(first, second), third);
			// create its string representation without parentheses 

			String formula = first.toString() + " EU " + second.toString() + " || " + third.toString();
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	// for All until tests
	@Test
	public void testForAllUntilForAllUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a AU b AU  c = a AU (b AU c)
			// combine the three
			Formula expected = new ForAllUntil(first, new ForAllUntil(second, third));
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

	
	
	/*
	 * Since AU appears before EU in the grammar, AU binds stronger than EU and, hence, we get : (a AU b) EU c. 
	 *  However, a EU b AU c should give rise to: a EU (b AU c)
	 */
//	@Test
//	public void testForAllUntilExistsUntil()
//	{
//		for (int c = 0; c < CASES; c++)
//		{
//			Formula first = Formula.random();
//			Formula second = Formula.random();
//			Formula third = Formula.random();
//
//			// a AU b EU  c = a AU (b EU c)
//			// combine the three
//			Formula expected = new  ExistsUntil(first, new ForAllUntil(second,third)); 
//			// create its string representation without parentheses 
//
//			String formula = first.toString() + " EU " + second.toString() + " AU " + third.toString();
//			// obtain the parse tree
//			ParseTree tree = parseCtl(formula);
//			// generate an abstract syntax tree from the parse tree
//			Formula actual = generator.visit(tree);
//			assertNotNull(actual);
//			assertEquals(expected, actual);
//		}
//	}


	@Test
	public void testForAllUntilNot()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new Not(first), second);
			// create its string representation without parentheses 

			String formula = "!"+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testForAllUntilForAllNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new ForAllNext(first), second);
			// create its string representation without parentheses 

			String formula = "AX "+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testForAllUntilForAllAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new ForAllAlways(first), second);
			// create its string representation without parentheses 

			String formula = "AG "+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testForAllUntilForAllEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new ForAllEventually(first), second);
			// create its string representation without parentheses 

			String formula = "AF "+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testForAllUntilExistsNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new ExistsNext(first), second);
			// create its string representation without parentheses 

			String formula = "EX "+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testForAllUntilExistsAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new ExistsAlways(first), second);
			// create its string representation without parentheses 

			String formula = "EG "+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testForAllUntilExistsEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ForAllUntil(new ExistsEventually(first), second);
			// create its string representation without parentheses 

			String formula = "EF "+ first.toString() + " AU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	// EU tests
	@Test
	public void testExistsUntilExistsUntil()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();
			Formula third = Formula.random();

			// a AU b AU  c = a AU (b AU c)
			// combine the three
			Formula expected = new ExistsUntil(first, new ExistsUntil(second, third));
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



	@Test
	public void testExistsUntilNot()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new Not(first), second);
			// create its string representation without parentheses 

			String formula = "!"+ first.toString() + " EU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testExistsUntilForAllNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new ForAllNext(first), second);
			// create its string representation without parentheses 

			String formula = "AX "+ first.toString() + " EU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testExistsUntilForAllAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new ForAllAlways(first), second);
			// create its string representation without parentheses 

			String formula = "AG "+ first.toString() + " EU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testExistsUntilForAllEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new ForAllEventually(first), second);
			// create its string representation without parentheses 

			String formula = "AF "+ first.toString() + " EU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testExistsUntilExistsNext()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new ExistsNext(first), second);
			// create its string representation without parentheses 

			String formula = "EX "+ first.toString() + " EU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testExistsUntilExistsAlways()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new ExistsAlways(first), second);
			// create its string representation without parentheses 

			String formula = "EG "+ first.toString() + " EU " + second.toString() ;
			// obtain the parse tree
			ParseTree tree = parseCtl(formula);
			// generate an abstract syntax tree from the parse tree
			Formula actual = generator.visit(tree);
			assertNotNull(actual);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testExistsUntilExistsEventually()
	{
		for (int c = 0; c < CASES; c++)
		{
			Formula first = Formula.random();
			Formula second = Formula.random();


			// ! a AU b = (!a) AU b
			// combine the three
			Formula expected = new ExistsUntil(new ExistsEventually(first), second);
			// create its string representation without parentheses 

			String formula = "EF "+ first.toString() + " EU " + second.toString() ;
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
	private ParseTree parseCtl(String formula) {
		CharStream input = CharStreams.fromString(formula);
		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		parser.removeErrorListeners();//remove ConsoleErrorListener

		parser.addErrorListener(new DialogListener());//add ours
		ParseTree tree = parser.formula();
		return tree;
	}
}