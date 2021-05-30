package partialtransitionsystemlistener;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Generates a random graph and the corresponding Java code.
 *
 * @author Franck van Breugel
 */
public class GraphAndCode {
	/**
	 * Generates random graph and the corresponding Java code.
	 *
	 * @param fileName name of the file: fileName.tra contains the graph and
	 *                fileName.java contains the Java code
	 * @param states number of states of the graph (should not be more than 300)
	 */
	public static void generate(String fileName, int states) {

		try {
			final Random RANDOM = new Random();

			PrintWriter graph = new PrintWriter("src/test/resources/graph/" + fileName + ".tra");
			PrintWriter code = new PrintWriter("src/test/code/" + fileName + ".java");
			
			code.println("package code;");
			code.println("import java.util.Random;");
			code.println("import partialtransitionsystemlistener.Graph;");
			code.println();
			code.printf("public class %s implements Graph {%n", fileName);
			code.println("  public void run() {");
			code.println("    final Random RANDOM = new Random();");
			code.println("    boolean done = false;");
			code.println("    int state = 0;");
			code.println("    while (!done) {");
			code.println("      switch (state) {");
			
			graph.printf("%d -> %d%n", -1, 0);
			
			for (int i = 0; i < states; i++) {
				int successors = RANDOM.nextInt((int) Math.pow(2, states));
				String bits = Integer.toBinaryString(successors);
				int number = 0; // number of successors
				for (int j = 1; j < bits.length(); j++) {
					if (bits.charAt(j) == '1') {
						number++;
					}
				}
				code.printf("        case %d:%n", i);
				if (number == 0) {
					code.println("          done = true;");
					code.println("          break;");
				} else {
					code.printf("          switch (RANDOM.nextInt(%d)) {%n", number);
					int j = 1;
					for (int n = 0; n < number; n++) {
						while (bits.charAt(j) == '0') {
							j++;
						}
						graph.printf("%d -> %d%n", i, j);
						code.printf("            case %d:%n", n);
						code.printf("              state = %d;%n", j);
						code.println("              break;");
						j++;
					}
					code.println("          };");
					code.println("          break;");
				}
			}
			code.println("      }");
			code.println("    }");
			code.println("  }");
			code.println("}");
			graph.close();
			code.close();
		} catch (FileNotFoundException e) {
			System.out.println("Something went wrong with the file");
		}
	}

}
