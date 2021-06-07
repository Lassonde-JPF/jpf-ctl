package partialtransitionsystemlistener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Graph {

	private static final long seed = System.currentTimeMillis();
	
	public static Map<Integer, List<Integer>> random(int nodes, int maxEdges) {
		Random r = new Random(seed);
		// Generate empty graph structure
		Map<Integer, List<Integer>> graph = new LinkedHashMap<Integer, List<Integer>>(nodes);
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
		return graph;
	}
}
