package partialtransitionsystemlistener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class PTSLTest extends TestJPF {

	private static final String fileName = PTSLTest.class.getName() + ".tra";

	private static String path;

	private static final int N = 2;
	private static final int MIN_NODES = 1;
	private static final int MAX_NODES = 10;
	private static final int MAX_EDGES = 3;

	private List<PartialTransitionSystem> partialTransitionSystems;

	private static String[] properties = new String[] {
			"+nhandler.spec.delegate=partialtransitionsystemlistener.PTSLTest.TransitionSystem",
			"+listener=partialtransitionsystemlistener.PartialTransitionSystemListener",
			"+partialtransitionsystemlistener.use_dot=false", "+partialtransitionsystemlistener.max_new_states=" };

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		path = System.getProperty("user.dir") + "/src/test/resources/";
		Files.createDirectories(Paths.get(path + "/tmp"));
	}

	@AfterClass
	public static void afterAll() throws IOException {
		File dottyFile = new File(fileName);
		if (!dottyFile.delete()) {
			System.err.println("File: " + dottyFile.getName() + " was not deleted");
		}
		Files.walk(Paths.get(path + "tmp/")).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

	@Test
	public void PartialTransitionSystemExtendTest() {
		partialTransitionSystems = new ArrayList<PartialTransitionSystem>();
		Random r = new Random();
		final Map<Integer, List<Integer>> graph = TransitionSystem();

		for (int i = 0; i < N; i++) {
			properties[properties.length - 1] = "+partialtransitionsystemlistener.max_new_states="
					+ (int) Math.pow(2, (i + 1));
			if (verifyNoPropertyViolation(properties)) {
				int state = 0;
				boolean done = false;
				while (!done) {
					// System.out.println("Current State: " + state);
					List<Integer> successors = graph.get(state);
					if (successors.isEmpty() || (successors.size() == 1 && successors.get(0) == state)) {
						done = true;
					} else {
						state = successors.get(r.nextInt(successors.size()));
					}
				}
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

	private Map<Integer, List<Integer>> TransitionSystem() {
		Random r = new Random();
		// Determine the depth of the tree
		int nodes = MIN_NODES + r.nextInt(MAX_NODES - MIN_NODES + 1);
		// Generate empty graph structure
		Map<Integer, List<Integer>> graph = new LinkedHashMap<Integer, List<Integer>>(nodes);
		// Begin graph generation
		IntStream.range(0, nodes).forEach(value -> {
			graph.computeIfAbsent(value, k -> new ArrayList<Integer>()).addAll(
					r.ints(0, nodes).distinct().limit(r.nextInt(MAX_EDGES + 1)).boxed().collect(Collectors.toList()));
		});
		System.out.println(graph);
		return graph;
	}

	private void assertPartialTransitionSystemCorrectness() {
		if (partialTransitionSystems.size() > 1) {
			try {
				for (int i = 1; i < partialTransitionSystems.size(); i++) {
					partialTransitionSystems.get(i).extend(partialTransitionSystems.get(i - 1));
				}
			} catch (PartialTransitionSystemException e) {
				e.printStackTrace();
				fail();
			}
		} else {
			assertNotNull(partialTransitionSystems.get(0));
		}
	}
}
