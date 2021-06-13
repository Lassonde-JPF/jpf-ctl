package partialtransitionsystemlistener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

/**
 * Test class responsible for testing the correctness of the
 * PartialTransitionSystemListener.
 * 
 * @author mattw
 *
 */
public class PTSLTest extends TestJPF {

	// Attributes relating to listener output and root directory
	private static final String fileName = PTSLTest.class.getName() + ".tra";

	// Set to true to use log approximation for max_new_states property or false to
	// iterate every possible PTS
	private static final boolean USE_LOG_APPROXIMATION = true;

	// Attributes relating to graph generation
	private static final int NODES = 100;
	private static final int MAX_EDGES = NODES;
	private static final int N = USE_LOG_APPROXIMATION ? (int) Math.ceil(Math.log(NODES * MAX_EDGES + 1) / Math.log(2))
			: NODES * MAX_EDGES + 1;

	// List to hold partial transition systems after each run of JPF
	private List<PartialTransitionSystem> partialTransitionSystems;

	// Properties to apply to jpf
	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener+=,partialtransitionsystemlistener.PartialTransitionSystemListener", "" };
	private final String max_new_states = "+partialtransitionsystemlistener.max_new_states=";

	/**
	 * Removes the .tra file after tests have been completed
	 * 
	 * @throws IOException
	 */
	@AfterClass
	public static void afterAll() throws IOException {
		File dottyFile = new File(fileName);
		if (!dottyFile.delete()) {
			System.err.println("File: " + dottyFile.getName() + " was not deleted");
		}
	}

	/**
	 * Initializes (essentially resets) the list of partial transition systems
	 * generated from the output of the listener before each new test.
	 */
	@Before
	public void beforeEach() {
		partialTransitionSystems = new ArrayList<PartialTransitionSystem>();
	}

	/**
	 * Tests a given, random graph with the PartialTransitionSystemListener and
	 * checks that for a given set of `max_new_states` values, the resulting partial
	 * transition systems all satisfy the properties defined in the
	 * PartialTransitionSystem class.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void PartialTransitionSystemExtendTest() throws FileNotFoundException {
		final Map<Integer, List<Integer>> graph = Graph.random(NODES);
		for (int i = 0; i < N; i++) {
			properties[properties.length - 1] = max_new_states
					+ (USE_LOG_APPROXIMATION ? (int) Math.pow(2, (i + 1)) : (i + 1));
			if (verifyNoPropertyViolation(properties)) {
				traverseGraph(graph);
			} else {
				assertPartialTransitionSystemCorrectness(new PartialTransitionSystem(fileName));
			}
		}
	}

	/**
	 * Tests a single, known, partial transition system against the output of the
	 * PartialTransitionSystemListener.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void knownTransitionSystemTest() throws FileNotFoundException {
		properties[properties.length - 1] = max_new_states + 3;
		if (verifyNoPropertyViolation(properties)) {
			Random random = new Random();
			int state = 0;
			switch (random.nextInt(5)) {
			case 0:
				state = 1;
				switch (random.nextInt(2)) {
				case 0:
					state = 11;
					break;
				case 1:
					state = 12;
					break;
				}
				break;
			case 1:
				state = 2;
				switch (random.nextInt(2)) {
				case 0:
					state = 21;
					break;
				case 1:
					state = 22;
					break;
				}
				break;
			case 2:
				state = 3;
				switch (random.nextInt(2)) {
				case 0:
					state = 31;
					break;
				case 1:
					state = 32;
					break;
				}
				break;
			case 3:
				state = 4;
				switch (random.nextInt(2)) {
				case 0:
					state = 41;
					break;
				case 1:
					state = 42;
					break;
				}
				break;
			case 4:
				state = 5;
				switch (random.nextInt(2)) {
				case 0:
					state = 51;
					break;
				case 1:
					state = 52;
					break;
				}
				break;
			}
		} else {
			assertPartialTransitionSystemCorrectness(new PartialTransitionSystem(fileName));
		}
	}

	/**
	 * Traverses the given graph in such a way that JPF will explore all possible
	 * paths when provided with the property `cg.enumerate_random = true`
	 * 
	 * @param graph - the given graph to traverse
	 */
	public void traverseGraph(Map<Integer, List<Integer>> graph) {
		int state = 0;
		boolean done = false;
		Random r = new Random();
		while (!done) {
			List<Integer> successors = graph.get(state);
			if (successors.isEmpty()) {
				done = true;
			} else {
				state = successors.get(r.nextInt(successors.size()));
			}
		}
	}

	/**
	 * Asserts that for the given partial transition system `pts` all previous
	 * partial transition systems satisfy the properties defined in the
	 * PartialTransitionSystem class via the PartialTransitionSystem.extend method.
	 * 
	 * Moreover, after/if the assertion passes, the PartialTransisitonSystem pts is
	 * added to the list of current partial transition systems.
	 * 
	 * @param pts - the partial transition system to assert
	 */
	private void assertPartialTransitionSystemCorrectness(PartialTransitionSystem pts) {
		partialTransitionSystems.forEach(other -> {
			try {
				pts.extend(other);
			} catch (PartialTransitionSystemException e) {
				e.printStackTrace();
				fail();
			}
		});
		partialTransitionSystems.add(pts);
	}
}
