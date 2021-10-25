package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;

import algo.Model;
import algo.StateSets;
import config.LabelledPartialTransitionSystem;
import error.CTLError;
import error.FieldExists;

public class CounterExampleTest {
    	private Generator generator;
	
	private static final int NUM_ITERATIONS = 1;

	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}
    
	@Test
	void checkCounterExample() {

			LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
			//String input = Formula.random().toString();
			String input =  " AG (!EX algo.JavaFields.p3 || EX algo.JavaFields.p4) ";
			ParseTree tree = parseCtl(input);
			Formula formula = generator.visit(tree);
			
			Model m = new Model(pts);
			
			StateSets result = m.check(formula);

			System.out.println("Transition System:\n" + pts);
			System.out.println("Input formula:\n" + input);
			
			//m.printSubResult();
			
			System.out.println("Result:" + result);
			
			if(!result.getSat().contains(0))
			{
				System.out.print(m.getCounterExample(formula, 0));
			}
	}
	
	public StateSets test(String input, LabelledPartialTransitionSystem pts) {
		ParseTree tree = parseCtl(input);
		Formula formula = generator.visit(tree);
		return new Model(pts).check(formula);
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
