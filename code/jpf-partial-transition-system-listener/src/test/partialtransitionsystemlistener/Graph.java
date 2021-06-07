package partialtransitionsystemlistener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Graph {

	private static final long seed = System.currentTimeMillis();
	private Map<Integer, List<Integer>> graph;

	public Graph(int nodes, int maxEdges) {
		Random r = new Random(seed);
		// Generate empty graph structure
		graph = new LinkedHashMap<Integer, List<Integer>>(nodes);
		// Begin graph generation
		double probability = Math.log(nodes) / nodes;
		for (int i = 0; i < nodes; i++) {
			graph.put(i, new ArrayList<Integer>());
			for (int j = 0; j < nodes; j++) {
				if (r.nextDouble() < probability) {
					graph.get(i).add(j);
				}
			}
		}
		System.out.println(graph);
	}

	public void run() {
		int state = 0;
		boolean done = false;
		Random r = new Random();
		while (!done) {
			// System.out.println("Current State: " + state);
			List<Integer> successors = graph.get(state);
			if (successors.isEmpty()) {
				done = true;
			} else {
				state = successors.get(r.nextInt(successors.size()));
			}
		}
	}
}
