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

import algo.LabelledPartialTransitionSystem;
import algo.Model;
import algo.StateSets;
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
		
		ParseTree tree = parseCtl("AX EX java.lang.Integer.MAX_VALUE");
		Formula formula = generator.visit(tree);
		Model m = new Model(pts);

		StateSets T = m.check(formula);

		Set<Integer> Sat = new HashSet<Integer>();
		pts.getLabelling().entrySet().forEach(entry -> {
			if (entry.getValue().contains(java.lang.Integer.MAX_VALUE)
					|| entry.getValue().contains(java.lang.Integer.MIN_VALUE)) {
				Sat.add(entry.getKey());
			}
		});

		if(!T.getSat().contains(0))
		{
			System.out.print(m.getCounterExample(formula, 0).toString());
		}
//		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
//		StateSets result = test("java.lang.Integer.MAX_VALUE || java.lang.Integer.MIN_VALUE", pts);
//		Set<Integer> Sat = new HashSet<Integer>();
//		pts.getLabelling().entrySet().forEach(entry -> {
//			if (entry.getValue().contains(java.lang.Integer.MAX_VALUE)
//					|| entry.getValue().contains(java.lang.Integer.MIN_VALUE)) {
//				Sat.add(entry.getKey());
//			}
//		});
//		assertEquals(Sat, result.getSat());
		
	}

	public StateSets test(String input, LabelledPartialTransitionSystem pts) {
		// System.out.println("Transition System:\n" + pts);
		ParseTree tree = parseCtl(input);
		Formula formula = generator.visit(tree);
		// System.out.println("Input formula:\n" + input);
		StateSets ss = new Model(pts).check(formula);
	
		// System.out.println("Result:");
		// System.out.println(ss);
		return ss;
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
