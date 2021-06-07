package partialtransitionsystemlistener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Wrapper class for the static method `random` which generates a random
 * digraph.
 * 
 * @author Matthew Walker
 */
public class Graph {

	// Seed used for generating the random graph; needs to be explicitly set to keep
	// the graph the same between subsequent JPF calls
	private static final long seed = System.currentTimeMillis();

	/**
	 * Generates a random digraph in the form of a `Map<Integer, List<Integer>>`
	 * 
	 * @param nodes    - the number of nodes the graph should contain
	 * @param maxEdges - the maximum number of edges per node
	 * @return Map<Integer, List<Integer>> - the generated graph
	 */
	public static Map<Integer, List<Integer>> random(int nodes, int maxEdges) {
		Random r = new Random(seed);
		// Generate empty graph structure
		Map<Integer, List<Integer>> graph = new LinkedHashMap<Integer, List<Integer>>(nodes);
		// Generate the probability that a node should contain an edge
		double probability = Math.log(nodes) / nodes;
		// Begin graph generation
		for (int source = 0; source < nodes; source++) {
			// Add a new node to the graph
			graph.put(source, new ArrayList<Integer>());
			for (int target = 0; target < nodes; target++) {
				if (r.nextDouble() < probability) {
					// Add an edge from the new node `source` to the node `target`
					graph.get(source).add(target);
				}
			}
		}
		return graph;
	}
}
