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

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import error.MyError;
import error.MyErrorListener;
import error.MyErrorStrategy;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
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
	private final static String ctlTrue1	= "true";
	private final static String ctlTrue2	= "True";
	private final static String ctlFalse1	= "false";
	private final static String ctlFalse2	= "False";
	private final static String ctlAp		= "java.lang.Exception";
	private final static String[] atomics	= {ctlTrue1, ctlFalse1,  ctlAp};
	private static ArrayList<String> ctlNot, ctlAnd, ctlOr, ctlImplies, ctlIff;
	private static ArrayList<String> ctlForAllNext, ctlForAllEventually, ctlForAllAlways, ctlForAllUntil;
	private static ArrayList<String> ctlExistsNext, ctlExistsEventually, ctlExistsAlways, ctlExistsUntil;
	
	private Generator generator;
	
	private static final int CASES = 1000000;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ctlNot = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlNot.add("!" + atomic);
		}
		
		ctlAnd = new ArrayList<String>();
		for (String atomic1 : atomics) {
			String a1 = attachRandomBraxkets(atomic1);
			for (String atomic2 : atomics) {
				String a2 = attachRandomBraxkets(atomic2);
				ctlAnd.add("(" + a1 + " && " + a2);
			}
		}
		
		ctlOr = new ArrayList<String>();
		for (String atomic1 : atomics) {
			for (String atomic2 : atomics) {
				ctlOr.add(atomic1 + " || " + atomic2);
			}
		}
		
		ctlImplies = new ArrayList<String>();
		for (String atomic1 : atomics) {
			for (String atomic2 : atomics) {
				ctlImplies.add(atomic1 + " -> " + atomic2);
			}
		}
		
		ctlIff = new ArrayList<String>();
		for (String atomic1 : atomics) {
			for (String atomic2 : atomics) {
				ctlIff.add(atomic1 + " <-> " + atomic2);
			}
		}
		
		ctlForAllNext = new ArrayList<String>();
		for (String atomic : atomics) {
			String a = attachRandomBraxkets(" AX " + atomic);
			ctlForAllNext.add(a);
		}		
		
		ctlForAllEventually = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlForAllEventually.add("A F " + atomic);
		}
		
		ctlForAllAlways = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlForAllAlways.add("A G " + atomic);
		}
		
		ctlForAllUntil = new ArrayList<String>();
		for (String atomic1 : atomics) {
			String a1 = attachRandomBraxkets(atomic1);
			for (String atomic2 : atomics) {
				String a2 = attachRandomBraxkets(atomic2);
				ctlForAllUntil.add(a1 + " AU " + a2 );
			}
		}		
		
		ctlExistsNext = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlExistsNext.add("E X " + atomic);
		}
		
		ctlExistsEventually = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlExistsEventually.add("E F " + atomic);
		}
		
		ctlExistsAlways = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlExistsAlways.add("E G " + atomic);
		}
		
		ctlExistsUntil = new ArrayList<String>();
		for (String atomic1 : atomics) {
			for (String atomic2 : atomics) {
				ctlExistsUntil.add("E (" + atomic1 + " U " + atomic2 + ")");
			}
		}		
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
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
	void testReservedWordsError() {
		String ctl1	= "( for.x & new.while )   ";		
		Formula formula1 = generator.visit(parseCtl(ctl1));
		
		assertNotNull(formula1);
		
	}

	
	/*@Test
	void testOperatorError() {
		String ctl1	= "( for && for.x )   ";		
		Formula formula1 = generator.visit(parseCtl(ctl1));
		
		assertNotNull(formula1);
		
	}*/
	
	/**
	 * Tests the {@code visitTrue} method. It ensures that the method returns
	 * 	an instance of class {@code True} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitTrue() {
		//String ctlTrue = attachRandomBraxkets(ctlTrue1);
		//Formula formula1 = generator.visit(parseCtl(ctlTrue));
		//assertNotNull(formula1);
		//assertEquals(True.class, formula1.getClass());
		
	}
	
	/**
	 * Tests the {@code visitAnd} method. It ensures that the method returns
	 * 	an instance of class {@code And} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitAnd() {
		for (String andFormula : ctlAnd) {
			//Formula formula1 = generator.visit(parseCtl(andFormula));
			//assertNotNull(formula1);
			//assertEquals(And.class, formula1.getClass());
				
		}
	}
	
	/**
	 * Tests the {@code visitForAllNext} method. It ensures that the method returns
	 * 	an instance of class {@code ForAll} containing an object of class {@code Next}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitForAllNext() {
		for (String forAllNextFormula : ctlForAllNext) {
			//ForAllNext formula1 = (ForAllNext) generator.visit(parseCtl(forAllNextFormula));
			//assertNotNull(formula1);	
			//assertEquals(ForAllNext.class, formula1.getClass());
		}
	}
	
	/**
	 * Tests the {@code visitForAllNext} method. It ensures that the method returns
	 * 	an instance of class {@code ForAll} containing an object of class {@code Until}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitForAllUntil() {
		for (String forAllUntilFormula : ctlForAllUntil) {
			//ForAllUntil formula1 = (ForAllUntil) generator.visit(parseCtl(forAllUntilFormula));
			//assertNotNull(formula1);	
			//assertEquals(ForAllUntil.class, formula1.getClass());
		}
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
	 * 
	 * Translates a syntactically correct CTL formula from its String form to a
	 * ParseTree.
	 * 
	 * @param ctlFormula
	 * @return The ParseTree representation of the given CTL formula
	 */
	private ParseTree parseCtl(String formula) {
		CharStream input = CharStreams.fromString(formula);
		MyError error = new MyError();
		input =  error.errorCheckAndRecover(input);

		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		parser.removeErrorListeners();//remove ConsoleErrorListener
		
		parser.addErrorListener(new MyErrorListener());//add ours
		//parser.setErrorHandler(new MyErrorStrategy());
		
		ParseTree tree = parser.formula();
		return tree;
	}
	
	private static String attachRandomBraxkets(String input)
	{
		Random rand = new Random();
		int l = rand.nextInt(50);		//number of left brackets
		int r = rand.nextInt(50);		//number of right brackets
		StringBuilder s = new StringBuilder();
		
		for(int i = 0; i < r; i++)
		{
			s.append("(");
		}
		s.append(input);
		for(int i = 0; i < l; i++)
		{
			s.append(")");
		}
		return s.toString();
	}
	
}
