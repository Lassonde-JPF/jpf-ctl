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
		IntStream.range(0, nodes).forEach(value -> {
			graph.computeIfAbsent(value, k -> new ArrayList<Integer>()).addAll(
					r.ints(0, nodes).distinct().limit(r.nextInt(maxEdges + 1)).boxed().collect(Collectors.toList()));
		});
		System.out.println(graph);
	}

	public void run() {
		int state = 0;
		boolean done = false;
		Random r = new Random();
		while (!done) {
			//System.out.println("Current State: " + state);
			List<Integer> successors = graph.get(state);
			if (successors.isEmpty()) {
				done = true;
			} else {
				state = successors.get(r.nextInt(successors.size()));
			}
		}
	}
}
