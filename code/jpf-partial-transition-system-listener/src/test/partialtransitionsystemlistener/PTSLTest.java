package partialtransitionsystemlistener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class PTSLTest extends TestJPF {

	// Attributes relating to listener output and root directory
	private static final String fileName = PTSLTest.class.getName() + ".tra";
	private static String path;

	// Set to true to use log approximation for max_new_states property or false to
	// iterate every possible PTS
	private static final boolean USE_LOG_APPROXIMATION = true;

	// Attributes relating to graph generation
	private static final int NODES = 100;
	private static final int MAX_EDGES = (int) Math.ceil(Math.log(NODES) / Math.log(2));
	private static final int N = USE_LOG_APPROXIMATION ? (int) Math.ceil(Math.log(NODES * MAX_EDGES + 1) / Math.log(2))
			: NODES * MAX_EDGES + 1;

	// List to hold partial transition systems after each run of JPF
	private List<PartialTransitionSystem> partialTransitionSystems = new ArrayList<PartialTransitionSystem>();

	// Properties to apply to jpf
	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener+=,partialtransitionsystemlistener.PartialTransitionSystemListener", "" // dummy property for
																								// max_new_states
	};
	private final String max_new_states = "+partialtransitionsystemlistener.max_new_states=";

	/**
	 * Sets up this class before all tests. Initializes the `path` property.
	 * 
	 * @throws IOException
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		path = System.getProperty("user.dir") + "/src/test/resources/";
	}

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
	 * Tests a given graph with the PartialTransitionSystemListener and checks that
	 * for a given set of `max_new_states` values, the resulting partial transition
	 * systems all satisfy the properties defined in the PartialTransitionSystem
	 * class.
	 */
	@Test
	public void PartialTransitionSystemExtendTest() {
		final Map<Integer, List<Integer>> graph = Graph.random(NODES, MAX_EDGES);
		for (int i = 0; i < N; i++) {
			properties[properties.length - 1] = max_new_states
					+ (USE_LOG_APPROXIMATION ? (int) Math.pow(2, (i + 1)) : (i + 1));
			if (verifyNoPropertyViolation(properties)) {
				this.traverseGraph(graph);
			} else {
				try {
					partialTransitionSystems.add(new PartialTransitionSystem(fileName));
					assertPartialTransitionSystemCorrectness();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					fail();
				}
			}
		}
	}

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

	private void assertPartialTransitionSystemCorrectness() {
		try {
			for (int i = 1; i < partialTransitionSystems.size(); i++) {
				partialTransitionSystems.get(i).extend(partialTransitionSystems.get(i - 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
