package partialtransitionsystemlistener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import code.Graph;
import gov.nasa.jpf.util.test.TestJPF;

public class ListenerTest extends TestJPF {

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener=partialtransitionsystemlistener.PartialTransitionSystemListener",
			"+partialtransitionsystemlistener.use_dot=false", "+partialtransitionsystemlistener.max_new_states=" };

	private static final int N = 10;
	private static final int MIN_STATES = 1;
	private static final int MAX_STATES = 30;

	private static final String qualifiedPrefix = "/src/test/code/";

	private static ArrayList<Graph> graphs = new ArrayList<Graph>(N);

	@BeforeClass
	public static void setup() {
		Random r = new Random();
		System.out.println("Generating Graphs + Code ... ");
		for (int i = 0; i < N; i++) {
			try {
				// Generate .java file
				GraphAndCode.generate("graph" + i, MIN_STATES + r.nextInt(MAX_STATES - MIN_STATES + 1));
				// Compile .java file into a .class file
				String path = qualifiedPrefix + "graph" + i + ".java";
				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
				compiler.run(null, null, null, path);
				// Load the compiled class
				Class<?> graph = Class.forName(path, true, Thread.currentThread().getContextClassLoader());
				// push an instantiated instance of Graph to the list of graphs
				graphs.add((Graph) graph.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void cleanup() {
		// TODO remove all files from code and graph directories
	}

	@Test
	public void testSingleDigraph() {
		properties[3] = "+partialtransitionsystemlistener.max_new_states=" + 10000;
		for (Graph g : graphs) {
			if (verifyNoPropertyViolation(properties)) {
				g.main(null);
			} else {
				assertTrue(true); // replace with assertEquals(.../graph0.tra, out.tra);
			}
		}

	}
}
