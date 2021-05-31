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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import errors.MyErrorStrategy;
import errors.DialogListener;
import errors.MyErrorListener;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;


import parser.CTLLexer;
import parser.CTLParser;

/**
 * 
 * This is a JUnit Test class for the Generator class. It ensures that every
 * method in the Generator class functions as expected.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 */

class GeneratorTest {

	private Generator generator;
	
	private static final int CASES = 1000000;

	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}
	
	/*@Test
	void testRandom() {
	    for (int c = 0; c < CASES; c++) {
	        // generate a random abstract syntax tree
	        Formula randomFormula = Formula.random();
	        // obtain the parse tree of the textual representation of the abstract syntax tree
	        ParseTree tree = parseCtl(randomFormula.toString());
	        // generate an abstract syntax tree from the parse tree
	        Formula formula = generator.visit(tree);
	        assertNotNull(formula);
	        assertEquals(randomFormula, formula);
	    }
	}*/
	
	@Test
	void testError() {
		String ctlTrue1	= "( do )";
		Formula formula1 = generator.visit(parseCtl(ctlTrue1));
		assertNotNull(formula1);
		assertEquals(True.class, formula1.getClass());
		String ctlTrue2	= "( for )";
		Formula formula2 = generator.visit(parseCtl(ctlTrue2));
		//assertNotNull(formula2);
		//assertEquals(True.class, formula1.getClass());
	}
	/**
	 * Adds outer brackets to a given CTL formula.
	 * 
	 * @param ctlFormula
	 * @return A string representation of the CTL formula with extra outer brackets
	 */
	private String addBrackets(String ctlFormula) {
		return "(" + ctlFormula + ")";
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
		
		parser.addErrorListener(new MyErrorListener());//add ours
		parser.setErrorHandler(new MyErrorStrategy());
		ParseTree tree = parser.formula();
		return tree;
	}
	
}
