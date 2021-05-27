package partialtransitionsystemlistener;

import java.util.ArrayList;
import java.util.Random;

import org.junit.*;

import gov.nasa.jpf.util.test.TestJPF;

public class ListenerTest extends TestJPF {

	private static String[] properties = new String[] { "+cg.enumerate_random=true",
			"+listener=partialtransitionsystemlistener.PartialTransitionSystemListener",
			"+partialtransitionsystemlistener.use_dot=false", "+partialtransitionsystemlistener.max_new_states=" };

	private static ArrayList<Digraph> graphs = new ArrayList<Digraph>();

	private static final int N = 1;

	@BeforeClass
	public static void setUpBeforeClass() {
		for (int i = 0; i < N; i++) {
			graphs.add(new Digraph());
		}
	}

	@Test
	public void testSingleDigraph() {
		properties[3] = "+partialtransitionsystemlistener.max_new_states=" + 10000;
		for (Digraph d : graphs) {
			if (verifyNoPropertyViolation(properties)) {
			} else {
				assertTrue(true);
			}
		}
	}
}
