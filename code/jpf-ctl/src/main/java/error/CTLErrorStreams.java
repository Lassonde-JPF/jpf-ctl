package error;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

/**
 * A class to verify the input formula and recover it
 * 
 * @author Anto Nanah Ji
 *
 */
public class CTLErrorStreams {
	// Hash set of Java reserved words
	private HashSet<String> reservedWordsSet = new HashSet<>();
	// Hash set of operators
	private HashSet<String> operatorsSet = new HashSet<>();
	
	//Static regex
	private static final String SPACE = " ";
	private static final String DOT = ".";
	private static final String DOT_REGEX = "\\.";


	/**
	 * Inserts the Java reserved words into the reservedWordsSet Hash set. It also
	 * inserts the missing operators '&' and '|' into operatorsSet Hash set.
	 */
	public CTLErrorStreams() {
		// adding Java reserve words to the hash set
		reservedWordsSet.addAll(Arrays.asList(new String[] { "abstract", "assert", "boolean", "break", "byte", "case",
				"catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends",
				"final", "finally", "float", "for", "if", "goto", "implements", "import", "instanceof", "int",
				"interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short",
				"static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
				"void", "volatile", "while" }));

		// adding missing operators to the hash set
		operatorsSet.add("&");
		operatorsSet.add("|");
	}

	/**
	 * This method verifies the input formula if it contains any Java reserved
	 * words. It also checks if the input contains '&' or '|' characters. Finally,
	 * it recovers from the errors and returns the recovered input.
	 * 
	 * @param input - the input formula.
	 * @return recovered input if there is an error, the input formal otherwise.
	 */
	public CharStream errorCheckAndRecover(CharStream input) { 
		String output = Pattern.compile(SPACE).splitAsStream(input.toString())
				//If the current token is an AtomicProposition then iterate though identifiers
				.map(token -> token.contains(DOT) ? Pattern.compile(DOT_REGEX).splitAsStream(token)
						//If the current identifier is a reserved word then report and recover
						.map(identifier -> reservedWordsSet.contains(identifier) ? identifier.toUpperCase() : identifier)
						.collect(Collectors.joining(DOT)) : token)
				//If the current token is an operator (invalid) then report and recover
				.map(operator -> operatorsSet.contains(operator) ? operator+operator : operator)
				.collect(Collectors.joining(SPACE));

		System.out.println("Parsed Formula: " + output);
		return CharStreams.fromString(output);
	}

}
