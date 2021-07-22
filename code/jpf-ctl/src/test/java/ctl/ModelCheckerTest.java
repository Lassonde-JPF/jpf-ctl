package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
	void checkRandom() {
		StateSets result = test(Formula.random().toString(), new LabelledPartialTransitionSystem());
		assertNotNull(result);
	}

	@Test
	void checkTrue() {
		LabelledPartialTransitionSystem T = new LabelledPartialTransitionSystem();
		StateSets result = test("true", T);
		assertEquals(T.getStates(), result.getSat());
		assertEquals(true, result.getUnSat().isEmpty());
	}

	@Test
	void checkFalse() {
		LabelledPartialTransitionSystem F = new LabelledPartialTransitionSystem();
		StateSets result = test("false", F);
		assertEquals(F.getStates(), result.getUnSat());
		assertEquals(true, result.getSat().isEmpty());
	}

	/*
	 * This is an example (semi) complete test for And
	 */
	@Test
	void checkAnd() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true && true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertEquals(true, TT.getUnSat().isEmpty());
		
		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true && false", ptsTF);
		assertEquals(true, TF.getSat().isEmpty());
		assertEquals(ptsTF.getStates(), TF.getUnSat());
		
		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false && true", ptsFT);
		assertEquals(true, FT.getSat().isEmpty());
		assertEquals(ptsFT.getStates(), FT.getUnSat());
		
		assertEquals(TF, FT); //additionally TF and FT should be same
		
		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false && false", ptsFF);
		assertEquals(true, FF.getSat().isEmpty());
		assertEquals(ptsFF.getStates(), FF.getUnSat());
	}

	@Test
	void checkOr() { // TODO incomplete
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true || true", ptsTT);
		
		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true || false", ptsTF);
		
		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false || true", ptsFT);
		
		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false || false", ptsFF);
		
	}

	@Test
	void checkImplies() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true -> true", ptsTT);
		
		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true -> false", ptsTF);
		
		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false -> true", ptsFT);
		
		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false -> false", ptsFF);
	}

	@Test
	void checkIff() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true <-> true", ptsTT);
		
		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true <-> false", ptsTF);
		
		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false <-> true", ptsFT);
		
		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false <-> false", ptsFF);
	}

	@Test
	void checkExistsAlways() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("EG true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("EG false", ptsF);
	}

	@Test
	void checkExistsEventually() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("EF true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("EF false", ptsF);
	}

	@Test
	void checkExistsNext() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("EX true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("EX false", ptsF);
	}

	@Test
	void checkExistsUntil() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true EU true", ptsTT);
		
		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true EU false", ptsTF);
		
		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false EU true", ptsFT);
		
		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false EU false", ptsFF);
	}

	@Test
	void checkForAllAlways() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AG true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AG false", ptsF);
	}

	@Test
	void checkForAllEventually() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AF true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AF false", ptsF);
	}

	@Test
	void checkForAllNext() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AX true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AX false", ptsF);
	}

	@Test
	void checkForAllUntil() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true AU true", ptsTT);
		
		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true AU false", ptsTF);
		
		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false AU true", ptsFT);
		
		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false AU false", ptsFF);
	}

	@Test
	void checkNot() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("! true", ptsT);
		
		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("! false", ptsF);
	}

	@Test
	void checkAtomicProposition() { //TODO labelledpartialtransitionsystem needs to be fixed first
		//StateSets result = test("java.lang.Integer.MAX_VALUE", new LabelledPartialTransitionSystem());
	}

	public StateSets test(String input, LabelledPartialTransitionSystem pts) {
		System.out.println("Transition System:\n" + pts);
		ParseTree tree = parseCtl(input);
		Formula formula = generator.visit(tree);
		System.out.println("Input formula:\n" + input);
		StateSets ss = new Model().check(pts, formula);
		System.out.println("Result:");
		System.out.println(ss);
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
