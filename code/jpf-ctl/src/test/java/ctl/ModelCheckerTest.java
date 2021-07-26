package ctl;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import error.CTLError;
import error.CTLErrorStreams;
import error.FieldExists;
import error.MyError;

	
public class ModelCheckerTest {
	private Generator generator;
	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}
	
	/*@Test
	void ModelCheck()
	{
		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
		System.out.print(" Transition System: \n");
		System.out.print(pts.toString());
		//System.out.print(pts.toDot());
		
		Formula randomFormula = Formula.random();	
		ParseTree tree = parseCtl(randomFormula.toString());
		Formula formula = generator.visit(tree);
		System.out.print(" Input formula: \n");
		System.out.print(randomFormula.toString());
		
		Model m = new Model();
		System.out.print(" Result: \n");
		System.out.print(m.check(pts, formula).toString());
	}*/
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
		MyError error = new MyError(input);
		input =  error.errorCheckAndRecover();
		

		
		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);
		
		ParseTree tree = parser.formula();
		
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(new FieldExists(), tree);
		

		return tree;
	}
}
