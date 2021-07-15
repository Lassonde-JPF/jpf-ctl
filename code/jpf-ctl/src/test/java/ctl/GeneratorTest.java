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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import error.CTLError;
import error.FieldExists;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.ctl.*;

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
	 * Tests that the and operator has a higher precedence than the or operator.
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}
	
	 @Test 
	 void testRandom()
	 { 
		 for (int c = 0; c < CASES; c++) 
		 { // generate a random abstract syntax tree 
			 Formula randomFormula = Formula.random(); // obtain the parse tree of the textual representation of the abstract syntax tree 
			 ParseTree tree = parseCtl(randomFormula.toString()); // generate an abstract syntax tree from the parse tree 
			 Formula formula = generator.visit(tree); 
			 assertNotNull(formula); 
			 assertEquals(randomFormula, formula); 
		 } 
	 }
	 

	/**
	 * 
	 * Translates a syntactically correct CTL formula from its String form to a
	 * ParseTree.
	 * 
	 * @param ctlFormula
	 * @return The ParseTree representation of the given CTL formula
	 */
	private ParseTree parseCtl(String formula) {
		CharStream input = CharStreams.fromString(formula);
		CTLError error = new CTLError();
		input = error.errorCheckAndRecover(input);

		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		
		ParseTree tree = parser.formula();
		
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(new FieldExists(), tree);

		return tree;
	}
	
}
