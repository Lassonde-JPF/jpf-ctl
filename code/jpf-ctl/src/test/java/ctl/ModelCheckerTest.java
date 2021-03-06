package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import algo.Transition;
import error.CTLError;
import error.FieldExists;

public class ModelCheckerTest {
	private Generator generator;
	
	private static final int NUM_ITERATIONS = 20;

	/**
	 * Creates a Generator object for use by each test case
	 */
	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
	}
	
	@Test
	void testLabelledPartialTransitionSystemConstructor() throws IOException {
		String pathPrefix = "src/test/resources/testConstructor/";
		String testLabelFileName = pathPrefix + "testLabelFile";
		String testListenerFileName = pathPrefix + "testListenerFile";
		
		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem(testLabelFileName, testListenerFileName);
		
		Set<Transition> expectedTransitions = new HashSet<Transition>();
		expectedTransitions.add(new Transition(-1, 0));
		expectedTransitions.add(new Transition(0, 1));
		expectedTransitions.add(new Transition(1, 2));
		expectedTransitions.add(new Transition(1, 3));
		expectedTransitions.add(new Transition(2, 4));
		
		Set<Integer> expectedPartial = new HashSet<Integer>();
		expectedPartial.add(3);
		expectedPartial.add(5);
		
		Map<Integer, Set<Integer>> expectedLabelling = new HashMap<Integer, Set<Integer>>();
		expectedLabelling.put(1, new HashSet<Integer>());
		expectedLabelling.get(1).add(1);
		expectedLabelling.put(2, new HashSet<Integer>());
		expectedLabelling.get(2).add(1);
		expectedLabelling.get(2).add(2);
		expectedLabelling.put(3, new HashSet<Integer>());
		expectedLabelling.get(3).add(1);
		expectedLabelling.put(4, new HashSet<Integer>());
		expectedLabelling.get(4).add(1);
		
		Map<String, Integer> expectedFields = new HashMap<String, Integer>();
		expectedFields.put("algo.JavaFields.p1", 1);
		expectedFields.put("algo.JavaFields.p2", 2);
		
		assertEquals(expectedTransitions, pts.getTransitions());
		assertEquals(expectedPartial, pts.getPartial());
		assertEquals(expectedLabelling, pts.getLabelling());
		assertEquals(expectedFields, pts.getFields());
	}

	@Test
	void checkRandom() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
			String input = Formula.random().toString();
			
			ParseTree tree = parseCtl(input);
			Formula formula = generator.visit(tree);
			
			Model m = new Model(pts);
			
			StateSets result = m.check(formula);

			System.out.println("Transition System:\n" + pts);
			System.out.println("Input formula:\n" + input);
			
			m.printSubResult();
			
			System.out.println("Result:" + result);

			toDot(pts, "Random" + i);
		}
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

		LabelledPartialTransitionSystem ptsTFFT = new LabelledPartialTransitionSystem();
		StateSets TF = test("true && false", ptsTFFT);
		assertEquals(true, TF.getSat().isEmpty());
		assertEquals(ptsTFFT.getStates(), TF.getUnSat());

		StateSets FT = test("false && true", ptsTFFT);
		assertEquals(true, FT.getSat().isEmpty());
		assertEquals(ptsTFFT.getStates(), FT.getUnSat());

		assertEquals(TF, FT);

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

		LabelledPartialTransitionSystem ptsTFFT = new LabelledPartialTransitionSystem();
		StateSets TF = test("true || false", ptsTFFT);
		assertEquals(ptsTFFT.getStates(), TF.getSat());
		assertEquals(true, TF.getUnSat().isEmpty());

		StateSets FT = test("false || true", ptsTFFT);
		assertEquals(ptsTFFT.getStates(), FT.getSat());
		assertEquals(true, FT.getUnSat().isEmpty());

		assertEquals(TF, FT);

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
		Set<Integer> expected = ptsT.getTransitions().stream()
				.map(t -> t.source)
				.collect(Collectors.toSet());
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
	void checkForAllAlways() {
		LabelledPartialTransitionSystem ptsT = new LabelledPartialTransitionSystem();
		StateSets T = test("AG true", ptsT);
		assertEquals(ptsT.getStates(), T.getSat());
		assertEquals(true, T.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsF = new LabelledPartialTransitionSystem();
		StateSets F = test("AG false", ptsF);
		assertEquals(ptsF.getStates(), F.getUnSat());
		assertEquals(true, F.getSat().isEmpty());
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
				.filter(s -> !ptsF.getTransitions().stream()
						.map(t -> t.source)
						.collect(Collectors.toSet())
						.contains(s))
				.collect(Collectors.toSet());
		assertEquals(expected, F.getSat());
	}

	@Test
	void checkForAllUntil() {
		LabelledPartialTransitionSystem ptsTT = new LabelledPartialTransitionSystem();
		StateSets TT = test("true AU true", ptsTT);
		assertEquals(ptsTT.getStates(), TT.getSat());
		assertTrue(TT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsTF = new LabelledPartialTransitionSystem();
		StateSets TF = test("true AU false", ptsTF);
		assertTrue(TF.getSat().isEmpty());
		

		LabelledPartialTransitionSystem ptsFT = new LabelledPartialTransitionSystem();
		StateSets FT = test("false AU true", ptsFT);
		assertEquals(ptsFT.getStates(), FT.getSat());
		assertTrue(FT.getUnSat().isEmpty());

		LabelledPartialTransitionSystem ptsFF = new LabelledPartialTransitionSystem();
		StateSets FF = test("false AU false", ptsFF);
		assertEquals(ptsFF.getStates(), FF.getUnSat());
		assertTrue(FF.getSat().isEmpty());
		
		//Specific Tests
	
		int states = 7;
		
		Set<Transition> transitions = new HashSet<Transition>();
		transitions.add(new Transition(0, 1));
		transitions.add(new Transition(1, 2));
		transitions.add(new Transition(0, 3));
		transitions.add(new Transition(3, 4));
		transitions.add(new Transition(4, 5));
		transitions.add(new Transition(4, 6));
		
		Set<Integer> partial = new HashSet<Integer>();
		Map<Integer, Set<Integer>> labelling = new HashMap<Integer, Set<Integer>>();
		
		labelling.put(0, new HashSet<Integer>());
		labelling.get(0).add(0);
		labelling.put(1, new HashSet<Integer>());
		labelling.get(1).add(0);
		labelling.put(2, new HashSet<Integer>());
		labelling.get(2).add(0);
		labelling.put(3, new HashSet<Integer>());
		labelling.get(3).add(0);
		labelling.put(4, new HashSet<Integer>());
		labelling.get(4).add(0);
		labelling.put(5, new HashSet<Integer>());
		labelling.get(5).add(1);
		labelling.put(6, new HashSet<Integer>());
		labelling.get(6).add(1);
		
		Map<String, Integer> fields = new HashMap<String, Integer>();
		String[] fieldNames = new String[] {
			"algo.JavaFields.p1",
			"algo.JavaFields.p2",
			"algo.JavaFields.p3",
			"algo.JavaFields.p4"
		};
		for (int i = 0; i < fieldNames.length; i++) {
			fields.put(fieldNames[i], i);
		}
		
		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem(states, transitions, partial, labelling, fields);
		
		StateSets result = test("algo.JavaFields.p1 AU algo.JavaFields.p2", pts);
		
		Set<Integer> expectedSat = new HashSet<Integer>();
		expectedSat.add(3);
		expectedSat.add(4);
		expectedSat.add(5);
		expectedSat.add(6);
		
		Set<Integer> expectedUnSat = new HashSet<Integer>();
		expectedUnSat.add(0);
		expectedUnSat.add(1);
		expectedUnSat.add(-2);
		expectedUnSat.add(2);
		
		toDot(pts, "AU");
		
		assertEquals(expectedSat, result.getSat());
		assertEquals(expectedUnSat, result.getUnSat());
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
	void checkAtomicProposition() {
		LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem();
		StateSets result = test("java.lang.Integer.MAX_VALUE || java.lang.Integer.MIN_VALUE", pts);
		Set<Integer> Sat = new HashSet<Integer>();
		pts.getLabelling().entrySet().forEach(entry -> {
			if (entry.getValue().contains(java.lang.Integer.MAX_VALUE)
					|| entry.getValue().contains(java.lang.Integer.MIN_VALUE)) {
				Sat.add(entry.getKey());
			}
		});
		assertEquals(Sat, result.getSat());
		toDot(pts, "checkAtomicProposition");
	}

	private void toDot(LabelledPartialTransitionSystem pts, String fileName) {
		String pathPrefix = "src/test/resources/toDot/";
		File file = new File(pathPrefix + ModelCheckerTest.class.getName() + "_" + fileName + ".dot");
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print(pts.toDot());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public StateSets test(String input, LabelledPartialTransitionSystem pts) {
		ParseTree tree = parseCtl(input);
		Formula formula = generator.visit(tree);
		StateSets ss = new Model(pts).check(formula);
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
