package ctl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import error.FieldExists;
import error.MyError;

import parser.CTLLexer;
import parser.CTLParser;

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
	void testFieldExists() {
		String ctl1	= " java.lang.Integer.MAX_VALUE  && java.lang.Integer.MAX_VALUE ";	
		ParseTree tree = parseCtl(ctl1);
		Formula formula1 = generator.visit(tree);
		assertNotNull(formula1);
	}
	
	@Test
	void testFieldExistsError() {
		String ctl1	= " f.c  && k.c ";	
		ParseTree tree = parseCtl(ctl1);
		Formula formula1 = generator.visit(tree);
		assertNotNull(formula1);		
	}

	@Test
	void testOperatorError() {
		String ctl1	= "( C.for && C.f2 & C.f3 & C.f3 )   ";	
		ParseTree tree = parseCtl(ctl1);
		Formula formula1 = generator.visit(tree);
		assertNotNull(formula1);

	}
	
	@Test
	void testReservedWordsError() {
		String ctl1	= "( C.for | new.while )   ";		
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
		MyError error = new MyError();
		input =  error.errorCheckAndRecover(input);
		

		
		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		
		ParseTree tree = parser.formula();
		
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(new FieldExists(), tree);

		return tree;
	}
	

	
}

