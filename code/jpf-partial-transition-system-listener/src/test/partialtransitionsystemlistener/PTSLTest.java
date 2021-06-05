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

	private static final int N = 7;

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
		for (int i = 0; i < N; i++) {
			properties[3] = "+partialtransitionsystemlistener.max_new_states=" + (i+1);
			if (verifyNoPropertyViolation(properties)) {
				Random r = new Random();
				int state = 0;
				if (r.nextBoolean()) {
					if (r.nextBoolean()) {
						state = 1;
					} else {
						state = 2;
					}
				} else {
					if (r.nextBoolean()) {
						state = 3;
					} else {
						state = 4;
					}
				}
				System.out.println("State: " + state);
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
		if (partialTransitionSystems.size() > 1) {
			try {
				//TODO probably don't need to check *all* of the previous transition systems -> just i-1 may suffice.
				for (int i = 0; i < partialTransitionSystems.size(); i++) {
					for (int j = 0; j < i; j++) {
						partialTransitionSystems.get(i).extend(partialTransitionSystems.get(j));
					}
				}
			} catch (PartialTransitionSystemException e) {
				e.printStackTrace();
				fail();
			}
		}

	}
}
