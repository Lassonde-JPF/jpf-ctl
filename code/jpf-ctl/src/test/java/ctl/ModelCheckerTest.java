package ctl;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;

import algo.LabelledPartialTransitionSystem;
import algo.Model;
import algo.Model.StateSets;
import error.CTLError;
import error.FieldExists;


public class ModelCheckerTest {
	private Generator generator;
	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}
	
	@Test
	void RandomModelCheckTest()
	{
		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
		System.out.println("Transition System:");
		System.out.println(pts.toString());
		
		Formula randomFormula = Formula.random();	
		ParseTree tree = parseCtl(randomFormula.toString());
		Formula formula = generator.visit(tree);
		System.out.println("Input formula:");
		System.out.println(randomFormula);
		
		Model m = new Model();
		StateSets ss = m.check(pts, formula);
		System.out.println("Result:");
		System.out.println(ss);
	}
	
	@Test
	void checkExistsUntil()
	{
		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
		System.out.println("Transition System:");
		System.out.println(pts.toString());
		
		String ExistsUntil = "true EU (EX true)";
		ParseTree tree = parseCtl(ExistsUntil);
		Formula formula = generator.visit(tree);
		System.out.println("Input formula:");
		System.out.println(ExistsUntil);
		
		Model m = new Model();
		StateSets ss = m.check(pts, formula);
		System.out.println("Result:");
		System.out.println(ss);
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
