package partialtransitionsystemlistener;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/*
 * TODO 
 *  - Enforce the states -1 and 0 are at the start of the generated graph and have a single edge between them
 *  - Store the generated digraph in a data structure
 *  - use stored data structure in the traverse() method to build a "java version" of the graph using switch / for / if statements
 */

public class Digraph {
	private static final int MIN_PER_RANK = 1; // Minimum Width
	private static final int MIN_RANKS = 1; // Minimum Height

	public static boolean generate(String fileName, int maxPerRank, int maxRanks, int percent) {
		int i, j, k, nodes = 0;

		Random r = new Random();

		// Calculate the number of ranks (height) of the digraph
		int ranks = MIN_RANKS + r.nextInt(maxRanks - MIN_RANKS + 1);

		try {
			PrintWriter graph = new PrintWriter("src/test/resources/graph/" + fileName + ".tra");
			PrintWriter code = new PrintWriter("src/test/resources/code/" + fileName + ".java");

			//Basic Code Template 
			
            code.println("import java.util.Random;");
            code.println();
            code.printf("public class %s {%n", fileName);
            code.println("  public static void main(String[] args) {");
            code.println("    final Random RANDOM = new Random();");
            code.println("    boolean done = false;");
            code.println("    int state = 0;");
            code.println("    while (!done) {");
            code.println("      switch (state) {");
			
			// Prepend the starting state that JPF creates
			graph.printf("%d -> %d%n", -1, 0);

			// Begin graph generation
			for (i = 0; i < ranks; i++) {
				int new_nodes = MIN_PER_RANK + r.nextInt(maxPerRank - MIN_PER_RANK + 1);

				code.printf("        case %d:%n", i);
				
				for (j = 0; j < nodes; j++) {
					for (k = 0; k < new_nodes; k++) {
						if (r.nextInt(100) < percent) {
							graph.printf("%d -> %d%n", j, (k + nodes));
							
						}
					}
				}
				nodes += new_nodes;
			}
			graph.close();
			code.close();
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Listener could not write to file " + fileName + " aborting graph generation");
			return false;
		}
	}
}
