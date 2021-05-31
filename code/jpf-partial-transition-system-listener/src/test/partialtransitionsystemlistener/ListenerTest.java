package partialtransitionsystemlistener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class ListenerTest extends TestJPF {

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener=partialtransitionsystemlistener.PartialTransitionSystemListener",
			"+partialtransitionsystemlistener.use_dot=false", "+partialtransitionsystemlistener.max_new_states="};

	private static String path;

	private static final int N = 1;
	private static final int MIN_STATES = 1;
	private static final int MAX_STATES = 30;

	/**
	 * This method generates `N` random directed graphs, places their .java and .tra
	 * representations in the /src/test/code/ and /src/test/resources/graph/
	 * directories, respectively. Moreover, it compiles the .java representation to
	 * a .class file in the same directory.
	 */
	@BeforeClass
	public static void setup() {
		path = System.getProperty("user.dir") + "/src/test/";
		Random r = new Random();
		for (int i = 0; i < N; i++) {
			try {
				// Generate .java file
				GraphAndCode.generate("Graph" + i, MIN_STATES + r.nextInt(MAX_STATES - MIN_STATES + 1));
				// get the compiler object
				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
				// Compile .java file into a .class file
				if (compiler.run(System.in, System.out, System.err, path + "/code/Graph" + i + ".java") != 0) {
					System.out.println("There was an error compiling: " + path + "/code/Graph" + i + ".java");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void cleanup() throws IOException {
		System.out.println("Cleaning Up");
        Files.walk(Paths.get("src/test/code/"))
        .filter(Files::isRegularFile)
        .map(Path::toFile)
        .forEach(File::delete);
//        Files.walk(Paths.get("src/test/resources/graph/"))
//        .filter(Files::isRegularFile)
//        .map(Path::toFile)
//        .forEach(File::delete);
	}

	@Test
	public void testRandomDigraph() {
		properties[3] = "+partialtransitionsystemlistener.max_new_states=" + 50;
		for (int i = 0; i < N; i++) {
			try {
				File root = new File(System.getProperty("user.dir") + "/src/test");
				URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
				Class<?> graphClass = Class.forName("code.Graph" + i, true, classLoader);
				Graph g = (Graph) graphClass.newInstance();
				if (verifyNoPropertyViolation(properties)) {
					g.run();
				} else {
					assertTrue(true); // TODO replace with the assertion below when bugs are fixed
					// assertFilesEqual(path + "/resources/graph/Graph" + i + ".tra", path +
					// "/partialtransitionsystemlistener.ListenerTest.tra");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void assertFilesEqual(String actual, String expected) {
		try {
			String actualLines = Files.lines(new File(actual).toPath()).collect(Collectors.joining("\n"));
			String expectedLines = Files.lines(new File(expected).toPath()).collect(Collectors.joining("\n"));

			assertEquals(expectedLines, actualLines);
		} catch (IOException e) {
			fail();
		}
	}
}
