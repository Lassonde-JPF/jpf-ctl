package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

		assertEquals(TF, FT); // additionally TF and FT should be same
		// TODO is this correct? It appears this statement is false (sometimes)

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false && false", ptsFF);
		assertEquals(true, FF.getSat().isEmpty());
		assertEquals(ptsFF.getStates(), FF.getUnSat());
	}

	@Test
	void checkOr() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true || true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertEquals(true, TT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true || false", ptsTF);
		assertEquals(ptsTF.getStates(), TF.getSat());
		assertEquals(true, TF.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false || true", ptsFT);
		assertEquals(ptsFT.getStates(), FT.getSat());
		assertEquals(true, FT.getUnSat().isEmpty());

		assertEquals(TF, FT); // additionally TF and FT should be same
		// TODO is this correct? It appears this statement is false (sometimes)

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false || false", ptsFF);
		assertEquals(ptsFF.getStates(), FF.getUnSat());
		assertEquals(true, FF.getSat().isEmpty());
	}

	@Test
	void checkImplies() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true -> true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertEquals(true, TT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true -> false", ptsTF);
		assertEquals(ptsTF.getStates(), TF.getUnSat());
		assertEquals(true, TF.getSat().isEmpty());

		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false -> true", ptsFT);
		assertEquals(ptsFT.getStates(), FT.getSat());
		assertEquals(true, FT.getUnSat().isEmpty());

		assertEquals(false, TF.equals(FT));

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false -> false", ptsFF);
		assertEquals(ptsFF.getStates(), FF.getSat());
		assertEquals(true, FF.getUnSat().isEmpty());
	}

	@Test
	void checkIff() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true <-> true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertEquals(true, TT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true <-> false", ptsTF);
		assertEquals(ptsTF.getStates(), TF.getUnSat());
		assertEquals(true, TF.getSat().isEmpty());

		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false <-> true", ptsFT);
		assertEquals(ptsFT.getStates(), FT.getUnSat());
		assertEquals(true, FT.getSat().isEmpty());

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false <-> false", ptsFF);
		assertEquals(ptsFF.getStates(), FF.getSat());
		assertEquals(true, FF.getUnSat().isEmpty());
	}

	@Test
	void checkExistsAlways() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("EG true", ptsT);
		assertEquals(ptsT.getStates(), T.getSat());
		assertEquals(true, T.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("EG false", ptsF);
		assertEquals(ptsF.getStates(), F.getUnSat());
		assertEquals(true, F.getSat().isEmpty());
	}

	@Test
	void checkExistsEventually() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("EF true", ptsT);
		assertEquals(ptsT.getStates(), T.getSat());
		assertEquals(true, T.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("EF false", ptsF);
		assertEquals(ptsF.getStates(), F.getUnSat());
		assertEquals(true, F.getSat().isEmpty());
	}

	@Test
	void checkExistsNext() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("EX true", ptsT);
		// Should be all states which have a transition (i.e are a source node)
		Set<Integer> expected = ptsT.getTransitions().stream().map(t -> t.source).collect(Collectors.toSet());
		assertEquals(expected, T.getSat());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("EX false", ptsF);
		assertEquals(ptsF.getStates(), F.getUnSat());
		assertEquals(true, F.getSat().isEmpty());
	}

	@Test
	void checkExistsUntil() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true EU true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertEquals(true, TT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true EU false", ptsTF);
		assertEquals(ptsTF.getStates(), TF.getUnSat());
		assertEquals(true, TF.getSat().isEmpty());

		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false EU true", ptsFT);
		assertEquals(ptsFT.getStates(), FT.getSat());
		assertEquals(true, FT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false EU false", ptsFF);
		assertEquals(ptsFF.getStates(), FF.getUnSat());
		assertEquals(true, FF.getSat().isEmpty());
	}

	@Test
	void checkForAllAlways() { // TODO I'm not sure what the expected result should be here
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AG true", ptsT);
		// assertEquals(ptsT.getStates(), T.getSat());
		// assertEquals(true, T.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AG false", ptsF);
		// assertEquals(ptsF.getStates(), F.getUnSat());
		// assertEquals(true, F.getSat().isEmpty());
	}

	@Test
	void checkForAllEventually() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AF true", ptsT);
		assertEquals(ptsT.getStates(), T.getSat());
		assertEquals(true, T.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AF false", ptsF);
		assertEquals(ptsF.getStates(), F.getUnSat());
		assertEquals(true, F.getSat().isEmpty());
	}

	@Test
	void checkForAllNext() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AX true", ptsT);
		assertEquals(ptsT.getStates(), T.getSat());
		assertEquals(true, T.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AX false", ptsF);
		Set<Integer> expected = ptsF.getStates().stream()
				.filter(s -> !ptsF.getTransitions().stream().map(t -> t.source).collect(Collectors.toSet()).contains(s))
				.collect(Collectors.toSet());
		assertEquals(expected, F.getSat());
	}

	@Test
	void checkForAllUntil() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true AU true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertEquals(true, TT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true AU false", ptsTF);
		// TODO this case I'm not sure. I tried all states which do not have a
		// transition (which i believe is what is expected) though the actual result was
		// different.

		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false AU true", ptsFT);
		assertEquals(ptsFT.getStates(), FT.getSat());
		assertEquals(true, FT.getUnSat());

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false AU false", ptsFF);
		assertEquals(ptsFF.getStates(), FF.getUnSat());
		assertEquals(true, FF.getSat());
	}

	@Test
	void checkNot() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("! true", ptsT);
		assertEquals(ptsT.getStates(), T.getUnSat());
		assertEquals(true, T.getSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("! false", ptsF);
		assertEquals(ptsF.getStates(), F.getSat());
		assertEquals(true, F.getUnSat().isEmpty());
	}

	@Test
	void checkAtomicProposition() { // TODO labelledpartialtransitionsystem needs to be fixed first
		// StateSets result = test("java.lang.Integer.MAX_VALUE", new
		// LabelledPartialTransitionSystem());
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
