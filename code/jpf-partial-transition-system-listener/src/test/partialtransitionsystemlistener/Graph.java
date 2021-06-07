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
	
	private static final int MIN_NODES = 3;
	private static final int MAX_NODES = 5;
	private static final int MAX_EDGES = 3;

	private Map<Integer, List<Integer>> graph;

	public Graph() {
		Random r = new Random(seed);
		// Determine the depth of the tree
		int nodes = MIN_NODES + r.nextInt(MAX_NODES - MIN_NODES + 1);
		// Generate empty graph structure
		graph = new LinkedHashMap<Integer, List<Integer>>(nodes);
		// Begin graph generation
//		IntStream.range(0, nodes).forEach(value -> {
//			graph.computeIfAbsent(value, k -> new ArrayList<Integer>()).addAll(
//					r.ints(0, nodes).distinct().limit(r.nextInt(MAX_EDGES + 1)).boxed().collect(Collectors.toList()));
//		});
		for (int i = 0; i < nodes; i++) {
			graph.put(i, new ArrayList<Integer>());
			for (int j = 0; j < nodes; j++) {
				if (r.nextDouble() < 0.7) {
					graph.get(i).add(j);
				}
			}
		}
		System.out.println(graph);
	}

	//TODO needs to be more robust i.e detect cycles and stop
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
