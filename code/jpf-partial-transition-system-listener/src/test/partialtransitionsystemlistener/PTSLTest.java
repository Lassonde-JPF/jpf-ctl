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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class PTSLTest extends TestJPF {

	private static final String fileName = PTSLTest.class.getName() + ".tra";

	private static String path;

	private static final int N = 16;

	private static final int MIN_DEPTH = 1;
	private static final int MAX_DEPTH = 5;
	private static final int MIN_WIDTH = 1;
	private static final int MAX_WIDTH = 5;

	private List<PartialTransitionSystem> partialTransitionSystems;

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
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
		int depth = MIN_DEPTH + r.nextInt(MAX_DEPTH - MIN_DEPTH + 1);
		int width = MIN_WIDTH + r.nextInt(MAX_WIDTH - MIN_WIDTH + 1);
		for (int i = 0; i < N; i++) {
			properties[3] = "+partialtransitionsystemlistener.max_new_states=" + (int) Math.pow(2, (i+1));
			if (verifyNoPropertyViolation(properties)) {
				TransitionSystem(depth, width);
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

	public int TransitionSystem(int depth, int width) {
		Random r = new Random();
		int state = 0;
		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < width; j++) {
				if (r.nextBoolean()) {
					state += (j + i);
				} else {
					state -= (j + i);
				}
			}
		}
		return state;
	}

	private void assertPartialTransitionSystemCorrectness() {
		if (partialTransitionSystems.size() > 1) {
			try {
				for (int i = 1; i < partialTransitionSystems.size(); i++) {
					partialTransitionSystems.get(i).extend(partialTransitionSystems.get(i-1));
				}
			} catch (PartialTransitionSystemException e) {
				e.printStackTrace();
				fail();
			}
		}

	}
}
