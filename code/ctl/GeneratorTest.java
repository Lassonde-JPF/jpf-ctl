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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ctl.Always;
import ctl.And;
import ctl.AtomicProposition;
import ctl.Eventually;
import ctl.Exists;
import ctl.False;
import ctl.ForAll;
import ctl.Formula;
import ctl.Generator;
import ctl.Iff;
import ctl.Implies;
import ctl.Next;
import ctl.Not;
import ctl.Or;
import ctl.True;
import ctl.Until;
import parser.CTLLexer;
import parser.CTLParser;

/**
 * 
 * This is a JUnit Test class for the Generator class. It ensures that every method
 * in the Generator class functions as expected.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 */
class GeneratorTest {

	// The following fields will store string representations of every CTL formula
	//	as defined by the grammar.
	private final static String ctlTrue1	= "true";
	private final static String ctlTrue2	= "True";
	private final static String ctlFalse1	= "false";
	private final static String ctlFalse2	= "False";
	private final static String ctlAp		= "java.lang.Exception";
	private final static String[] atomics	= {ctlTrue1, ctlTrue2, ctlFalse1, ctlFalse2, ctlAp};
	private static ArrayList<String> ctlNot, ctlAnd, ctlOr, ctlImplies, ctlIff;
	private static ArrayList<String> ctlForAllNext, ctlForAllEventually, ctlForAllAlways, ctlForAllUntil;
	private static ArrayList<String> ctlExistsNext, ctlExistsEventually, ctlExistsAlways, ctlExistsUntil;
	
	private Generator generator;
	
	/**
	 * Creates every permutation of a CTL formula
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ctlNot = new ArrayList<String>();
		for (String atomic : atomics) {
			ctlNot.add("!" + atomic);
		}
		
		ctlAnd = new ArrayList<String>();
		for (String atomic1 : atomics) {
			for (String atomic2 : atomics) {
				ctlAnd.add(atomic1 + " && " + atomic2);
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
			ctlForAllNext.add("A X " + atomic);
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
			for (String atomic2 : atomics) {
				ctlForAllUntil.add("A (" + atomic1 + " U " + atomic2 + ")");
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

	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Tests the {@code visitTrue} method. It ensures that the method returns
	 * 	an instance of class {@code True} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitTrue() {
		Formula formula1 = generator.visit(parseCtl(ctlTrue1));
		assertNotNull(formula1);
		assertEquals(True.class, formula1.getClass());
		Formula formula2 = generator.visit(parseCtl(addBrackets(ctlTrue1)));
		assertNotNull(formula2);
		assertEquals(True.class, formula2.getClass());
		Formula formula3 = generator.visit(parseCtl(ctlTrue2));
		assertNotNull(formula3);
		assertEquals(True.class, formula3.getClass());
		Formula formula4 = generator.visit(parseCtl(addBrackets(ctlTrue2)));
		assertNotNull(formula4);
		assertEquals(True.class, formula4.getClass());
	}

	/**
	 * Tests the {@code visitFalse} method. It ensures that the method returns
	 * 	an instance of class {@code False} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitFalse() {
		Formula formula1 = generator.visit(parseCtl(ctlFalse1));
		assertNotNull(formula1);
		assertEquals(False.class, formula1.getClass());
		Formula formula2 = generator.visit(parseCtl(addBrackets(ctlFalse1)));
		assertNotNull(formula2);
		assertEquals(False.class, formula2.getClass());
		Formula formula3 = generator.visit(parseCtl(ctlFalse2));
		assertNotNull(formula3);
		assertEquals(False.class, formula3.getClass());
		Formula formula4 = generator.visit(parseCtl(addBrackets(ctlFalse2)));
		assertNotNull(formula4);
		assertEquals(False.class, formula4.getClass());		
	}
	
	/**
	 * Tests the {@code visitAtomicProposition} method. It ensures that the method
	 *	returns an instance of class {@code AtomicProposition} from the <i>ctl</i>
	 *	package.
	 */
	@Test
	void testVisitAtomicProposition() {
		Formula formula1 = generator.visit(parseCtl(ctlAp));
		assertNotNull(formula1);
		assertEquals(AtomicProposition.class, formula1.getClass());
		Formula formula2 = generator.visit(parseCtl(addBrackets(ctlAp)));
		assertNotNull(formula2);
		assertEquals(AtomicProposition.class, formula2.getClass());
	}
	
	/**
	 * Tests the {@code visitNot} method. It ensures that the method returns
	 * 	an instance of class {@code Not} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitNot() {
		for (String notFormula : ctlNot) {
			Formula formula1 = generator.visit(parseCtl(notFormula));
			assertNotNull(formula1);
			assertEquals(Not.class, formula1.getClass());
			Formula formula2 = generator.visit(parseCtl(addBrackets(notFormula)));
			assertNotNull(formula2);
			assertEquals(Not.class, formula2.getClass());		
		}
	}
	
	/**
	 * Tests the {@code visitAnd} method. It ensures that the method returns
	 * 	an instance of class {@code And} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitAnd() {
		for (String andFormula : ctlAnd) {
			Formula formula1 = generator.visit(parseCtl(andFormula));
			assertNotNull(formula1);
			assertEquals(And.class, formula1.getClass());
			Formula formula2 = generator.visit(parseCtl(addBrackets(andFormula)));
			assertNotNull(formula2);
			assertEquals(And.class, formula2.getClass());		
		}
	}

	/**
	 * Tests the {@code visitOr} method. It ensures that the method returns
	 * 	an instance of class {@code Or} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitOr() {
		for (String orFormula : ctlOr) {
			Formula formula1 = generator.visit(parseCtl(orFormula));
			assertNotNull(formula1);
			assertEquals(Or.class, formula1.getClass());
			Formula formula2 = generator.visit(parseCtl(addBrackets(orFormula)));
			assertNotNull(formula2);
			assertEquals(Or.class, formula2.getClass());		
		}
	}
	
	/**
	 * Tests the {@code visitImplies} method. It ensures that the method returns
	 * 	an instance of class {@code Implies} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitImplies() {
		for (String impliesFormula : ctlImplies) {
			Formula formula1 = generator.visit(parseCtl(impliesFormula));
			assertNotNull(formula1);
			assertEquals(Implies.class, formula1.getClass());
			Formula formula2 = generator.visit(parseCtl(addBrackets(impliesFormula)));
			assertNotNull(formula2);
			assertEquals(Implies.class, formula2.getClass());		
		}
	}
	
	/**
	 * Tests the {@code visitIff} method. It ensures that the method returns
	 * 	an instance of class {@code Iff} from the <i>ctl</i> package.
	 */
	@Test
	void testVisitIff() {
		for (String iffFormula : ctlIff) {
			Formula formula1 = generator.visit(parseCtl(iffFormula));
			assertNotNull(formula1);
			assertEquals(Iff.class, formula1.getClass());
			Formula formula2 = generator.visit(parseCtl(addBrackets(iffFormula)));
			assertNotNull(formula2);
			assertEquals(Iff.class, formula2.getClass());		
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
			ForAll formula1 = (ForAll) generator.visit(parseCtl(forAllNextFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(ForAll.class, formula1.getClass());
			assertEquals(Next.class, formula1.getInner().getClass());			
			ForAll formula2 = (ForAll) generator.visit(parseCtl(addBrackets(forAllNextFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(ForAll.class, formula2.getClass());
			assertEquals(Next.class, formula2.getInner().getClass());
		}
	}

	/**
	 * Tests the {@code visitForAllEventually} method. It ensures that the method returns
	 * 	an instance of class {@code ForAll} containing an object of class {@code Next}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitForAllEventually() {
		for (String forAllEventuallyFormula : ctlForAllEventually) {
			ForAll formula1 = (ForAll) generator.visit(parseCtl(forAllEventuallyFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(ForAll.class, formula1.getClass());
			assertEquals(Eventually.class, formula1.getInner().getClass());			
			ForAll formula2 = (ForAll) generator.visit(parseCtl(addBrackets(forAllEventuallyFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(ForAll.class, formula2.getClass());
			assertEquals(Eventually.class, formula2.getInner().getClass());
		}
	}
	
	/**
	 * Tests the {@code visitForAllAlways} method. It ensures that the method returns
	 * 	an instance of class {@code ForAll} containing an object of class {@code Always}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitForAllAlways() {
		for (String forAllAlwaysFormula : ctlForAllAlways) {
			ForAll formula1 = (ForAll) generator.visit(parseCtl(forAllAlwaysFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(ForAll.class, formula1.getClass());
			assertEquals(Always.class, formula1.getInner().getClass());			
			ForAll formula2 = (ForAll) generator.visit(parseCtl(addBrackets(forAllAlwaysFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(ForAll.class, formula2.getClass());
			assertEquals(Always.class, formula2.getInner().getClass());
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
			ForAll formula1 = (ForAll) generator.visit(parseCtl(forAllUntilFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(ForAll.class, formula1.getClass());
			assertEquals(Until.class, formula1.getInner().getClass());			
			ForAll formula2 = (ForAll) generator.visit(parseCtl(addBrackets(forAllUntilFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(ForAll.class, formula2.getClass());
			assertEquals(Until.class, formula2.getInner().getClass());
		}
	}
	
	/**
	 * Tests the {@code visitExistsNext} method. It ensures that the method returns
	 * 	an instance of class {@code Exists} containing an object of class {@code Next}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitExistsNext() {
		for (String existsNextFormula : ctlExistsNext) {
			Exists formula1 = (Exists) generator.visit(parseCtl(existsNextFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(Exists.class, formula1.getClass());
			assertEquals(Next.class, formula1.getInner().getClass());			
			Exists formula2 = (Exists) generator.visit(parseCtl(addBrackets(existsNextFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(Exists.class, formula2.getClass());
			assertEquals(Next.class, formula2.getInner().getClass());
		}
	}
	
	/**
	 * Tests the {@code visitExistsEventually} method. It ensures that the method returns
	 * 	an instance of class {@code Exists} containing an object of class {@code Eventually}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitExistsEventually() {
		for (String existsEventuallyFormula : ctlExistsEventually) {
			Exists formula1 = (Exists) generator.visit(parseCtl(existsEventuallyFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(Exists.class, formula1.getClass());
			assertEquals(Eventually.class, formula1.getInner().getClass());			
			Exists formula2 = (Exists) generator.visit(parseCtl(addBrackets(existsEventuallyFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(Exists.class, formula2.getClass());
			assertEquals(Eventually.class, formula2.getInner().getClass());
		}
	}
	
	/**
	 * Tests the {@code visitExistsAlways} method. It ensures that the method returns
	 * 	an instance of class {@code Exists} containing an object of class {@code Always}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitExistsAlways() {
		for (String existsAlwaysFormula : ctlExistsAlways) {
			Exists formula1 = (Exists) generator.visit(parseCtl(existsAlwaysFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(Exists.class, formula1.getClass());
			assertEquals(Always.class, formula1.getInner().getClass());			
			Exists formula2 = (Exists) generator.visit(parseCtl(addBrackets(existsAlwaysFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(Exists.class, formula2.getClass());
			assertEquals(Always.class, formula2.getInner().getClass());
		}
	}
	
	/**
	 * Tests the {@code visitExistsUntil} method. It ensures that the method returns
	 * 	an instance of class {@code Exists} containing an object of class {@code Until}
	 * 	both of which are from the <i>ctl</i> package.
	 */
	@Test
	void testVisitExistsUntil() {
		for (String existsUntilFormula : ctlExistsUntil) {
			Exists formula1 = (Exists) generator.visit(parseCtl(existsUntilFormula));
			assertNotNull(formula1);	
			assertNotNull(formula1.getInner());
			assertEquals(Exists.class, formula1.getClass());
			assertEquals(Until.class, formula1.getInner().getClass());			
			Exists formula2 = (Exists) generator.visit(parseCtl(addBrackets(existsUntilFormula)));
			assertNotNull(formula2);
			assertNotNull(formula2.getInner());
			assertEquals(Exists.class, formula2.getClass());
			assertEquals(Until.class, formula2.getInner().getClass());
		}
	}
	
	/**
	 * Adds outer brackets to a given CTL formula.
	 * @param	ctlFormula
	 * @return	A string representation of the CTL formula with extra outer brackets
	 */
	private String addBrackets(String ctlFormula) {
		return "(" + ctlFormula + ")";
	}
	
	/**
	 * Translates a syntactically correct CTL formula from its String form to a ParseTree.
	 * 
	 * @param 	ctlFormula
	 * @return	The ParseTree representation of the given CTL formula
	 */
	private ParseTree parseCtl(String formula) {
		CharStream input = CharStreams.fromString(formula);
		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		ParseTree tree = parser.root();
		return tree;
	}
}
