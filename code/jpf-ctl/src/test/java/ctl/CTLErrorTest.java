package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Random;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import error.CTLError;
import error.FieldExists;


public class CTLErrorTest {
	
	private Generator generator;
	
	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}



	@Test
	void testFieldExistsError() {
		String ctl1	= "java.lang.Integer.MIN_VALUE";	
		ParseTree tree = parseCtl(ctl1);
		Formula formula1 = generator.visit(tree);
		
	}
	
	@Test 
	 void testReservedWordError() 
	 { 
		 String ctl1 = "( for.x && while.new )   "; 
		 Formula formula1 = generator.visit(parseCtl(ctl1));
	 
		 assertNotNull(formula1);	 
	 }
	
	 @Test 
	 void testOperatorError() 
	 { 
		 String ctl1 = "( java.lang.Integer.f & C.new )   "; 
		 Formula formula1 = generator.visit(parseCtl(ctl1));
	 
		 assertNotNull(formula1);	 
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

		ParseTreeWalker ptw = new ParseTreeWalker();
		ptw.walk(new FieldExists(), tree);
	
		return tree;
	}

}
