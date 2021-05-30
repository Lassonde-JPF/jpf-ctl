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

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.nasa.jpf.util.test.TestJPF;

public class ListenerTest extends TestJPF {

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener=partialtransitionsystemlistener.PartialTransitionSystemListener",
			"+partialtransitionsystemlistener.use_dot=false", "+partialtransitionsystemlistener.max_new_states=" };

	private static final int N = 1;
	private static final int MIN_STATES = 1;
	private static final int MAX_STATES = 30;
	
	private static ArrayList<Graph> graphs = new ArrayList<Graph>(N);

	@BeforeClass
	public static void setup() {
		Random r = new Random();
		for (int i = 0; i < N; i++) {
			try {
				// Generate .java file
				GraphAndCode.generate("Graph" + i, MIN_STATES + r.nextInt(MAX_STATES - MIN_STATES + 1));
				// Get the path of the file to compile
				String path = new File("src/test/code/Graph" + i + ".java").getPath();
				// get the compiler object
				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
				// Compile .java file into a .class file
				if (compiler.run(System.in, System.out, System.err, path) == 0) {
					// Load the compiled class
					URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("src/test/").toURI().toURL() });
					Class<?> graph = Class.forName("code.Graph" + i, true, classLoader);
					// push an instantiated instance of Graph to the list of graphs
					graphs.add((Graph) graph.newInstance());
				} else {
					System.out.println("There was an error compiling: " + path);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void cleanup() throws IOException {
        Files.walk(Paths.get("src/test/code/"))
        .filter(Files::isRegularFile)
        .map(Path::toFile)
        .forEach(File::delete);
        Files.walk(Paths.get("src/test/resources/graph/"))
        .filter(Files::isRegularFile)
        .map(Path::toFile)
        .forEach(File::delete);
	}

	@Test
	public void testRandomDigraph() {
		properties[3] = "+partialtransitionsystemlistener.max_new_states=" + 50;
		for (Graph g : graphs) {
			System.out.println("Class: " + g.getClass().toString());
			if (verifyNoPropertyViolation(properties)) {
				g.run();
			} else {
				assertTrue(true); // replace with assertEquals(.../graph0.tra, out.tra);
			}
		}

	}
}
