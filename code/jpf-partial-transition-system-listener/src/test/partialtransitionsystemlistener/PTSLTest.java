package partialtransitionsystemlistener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
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

	public static final int N = 10;
	public static final int MIN = 1;
	public static final int MAX = 5;

	public static TreeMap<Integer, Integer> dimentions;

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener=partialtransitionsystemlistener.PartialTransitionSystemListener",
			"+partialtransitionsystemlistener.use_dot=false", "+partialtransitionsystemlistener.max_new_states=" };

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		path = System.getProperty("user.dir") + "/src/test/resources/";
		Files.createDirectories(Paths.get(path + "/tmp"));
		dimentions = new TreeMap<Integer, Integer>();
		Random r = new Random();
		for (int i = 0; i < N; i++) {
			dimentions.put(i, MIN + r.nextInt(MAX - MIN + 1));
		}
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
	public void TSCustom1a() {
		properties[3] = "+partialtransitionsystemlistener.max_new_states=" + 100;
		for (Entry<Integer, Integer> e : dimentions.entrySet()) {
			if (verifyNoPropertyViolation(properties)) {
				TSCustom(e.getKey(), e.getValue());
			} else {
				try {
					PartialTransitionSystem pts = new PartialTransitionSystem(fileName);
					System.out.println(pts.toString());
					assertNotNull(pts);
				} catch (FileNotFoundException e1) {
					fail();
				}
			}
		}
	}

	public void TSCustom(Integer width, Integer depth) {
		Random r = new Random();

		for (int i = 0; i < width; i++) {
			if (r.nextBoolean()) {
				for (int j = 0; j < depth; j++) {
					if (r.nextBoolean()) {
						String tmp = "Old McDonald ";
						tmp.length();
					} else {
						String tmp = "had a farm, ";
						tmp.length();
					}
				}
			} else {
				for (int j = 0; j < depth; j++) {
					if (r.nextBoolean()) {
						String tmp = "E-I-";
						tmp.length();
					} else {
						String tmp = "E-I-O ";
						tmp.length();
					}
				}
			}
		}
	}

	private void assertFilesEqual(String actual, String expected) {
		try {
			String actualLines = Files.lines(new File(actual).toPath()).collect(Collectors.joining("\n"));
			// String expectedLines = Files.lines(new
			// File(expected).toPath()).collect(Collectors.joining("\n"));

			// assertEquals(expectedLines, actualLines);
			assertNotNull(actualLines);
		} catch (IOException e) {
			fail();
		}
	}
}
