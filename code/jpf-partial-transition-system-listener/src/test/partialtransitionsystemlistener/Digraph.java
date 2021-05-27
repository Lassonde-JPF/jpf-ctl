package partialtransitionsystemlistener;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/*
 * TODO 
 *  - Enforce the states -1 and 0 are at the start of the generated graph and have a single edge between them
 *  - Store the generated digraph in a data structure
 *  - use stored data structure in the traverse() method to build a "java version" of the graph using switch / for / if statements
 */

public class Digraph {
	static final int MIN_PER_RANK = 1; // Width
	static final int MAX_PER_RANK = 5;
	static final int MIN_RANKS = 3; // Height
	static final int MAX_RANKS = 5;
	static final int PERCENT = 30; // Chance of Edge

	private PrintWriter writer;

	public Digraph() {
		int i, j, k, nodes = 0;

		Random r = new Random();

		int ranks = MIN_RANKS + r.nextInt(MAX_RANKS - MIN_RANKS + 1);

		String name = "Digraph.tra";
		try {
			this.writer = new PrintWriter(name);
		} catch (FileNotFoundException e) {
			System.err.println("Listener could not write to file " + name);
		}

		for (i = 0; i < ranks; i++) {
			int new_nodes = MIN_PER_RANK + r.nextInt(MAX_PER_RANK - MIN_PER_RANK + 1);

			for (j = 0; j < nodes; j++) {
				for (k = 0; k < new_nodes; k++) {
					if (r.nextInt(100) < PERCENT) {
						writer.printf("%d -> %d%n", j, (k + nodes));
					}
				}
			}
			nodes += new_nodes;
		}
		writer.flush();
		writer.close();
	}

	public void traverse() {
	}
}
