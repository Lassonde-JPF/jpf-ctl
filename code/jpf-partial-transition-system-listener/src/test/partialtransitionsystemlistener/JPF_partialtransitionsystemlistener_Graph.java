package partialtransitionsystemlistener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gov.nasa.jpf.annotation.MJI;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.NativePeer;
import nhandler.conversion.ConversionException;
import nhandler.conversion.jvm2jpf.JVM2JPFConverter;

public class JPF_partialtransitionsystemlistener_Graph extends NativePeer {
	private static final long seed = System.currentTimeMillis();
	
	@MJI
    public static int random__ID__Ljava_util_Map_2(MJIEnv env, int dummy, int nodes) throws ConversionException {
        // invoke Graph.random(number, probability)
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
        // represent graph in JPF and return its index
        return JVM2JPFConverter.obtainJPFObj(graph, env);
    }
}
