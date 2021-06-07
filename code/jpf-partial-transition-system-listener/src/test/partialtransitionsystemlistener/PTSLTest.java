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

	private static final int N = 15;

	private List<PartialTransitionSystem> partialTransitionSystems = new ArrayList<PartialTransitionSystem>();

	private static String[] properties = new String[] {
			"+cg.enumerate_random=true",
			"+listener+=,partialtransitionsystemlistener.PartialTransitionSystemListener",
			""
	};
	private final String max_new_states = "+partialtransitionsystemlistener.max_new_states=";
	private int value = 0;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		path = System.getProperty("user.dir") + "/src/test/resources/";
		Files.createDirectories(Paths.get(path + "/tmp"));
	}

	@AfterClass
	public static void afterAll() throws IOException {
//		File dottyFile = new File(fileName);
//		if (!dottyFile.delete()) {
//			System.err.println("File: " + dottyFile.getName() + " was not deleted");
//		}
		Files.walk(Paths.get	(path + "tmp/")).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}

	@Test
	public void PartialTransitionSystemExtendTest() {
		final Graph graph = new Graph();
		for (int i = 0; i < N; i++) {
			value = (int) Math.pow(2, (i + 1));
			properties[properties.length - 1] = max_new_states + value;
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
