package partialtransitionsystemlistener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class PTSLTest extends TestJPF {

	private static final String fileName = PTSLTest.class.getName() + ".tra";

	private static String path;

	private static final boolean USE_LOG_APPROXIMATION = true;

	private static final int NODES = 100;
	private static final int MAX_EDGES = (int) Math.ceil(Math.log(NODES) / Math.log(2));
	private static final int N = USE_LOG_APPROXIMATION ? (int) Math.ceil(Math.log(NODES * MAX_EDGES + 1) / Math.log(2))
			: NODES * MAX_EDGES + 1;

	private List<PartialTransitionSystem> partialTransitionSystems = new ArrayList<PartialTransitionSystem>();

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener+=,partialtransitionsystemlistener.PartialTransitionSystemListener", "" // dummy property for
																								// max_new_states
	};
	private final String max_new_states = "+partialtransitionsystemlistener.max_new_states=";

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
		final Graph graph = new Graph(NODES, MAX_EDGES);
		for (int i = 0; i < N; i++) {
			properties[properties.length - 1] = max_new_states
					+ (USE_LOG_APPROXIMATION ? (int) Math.pow(2, (i + 1)) : (i + 1));
			if (verifyNoPropertyViolation(properties)) {
				graph.run();
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
