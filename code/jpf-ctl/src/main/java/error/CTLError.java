package error;

import java.util.Arrays;
import java.util.HashSet;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

/**
 * A class to verify the input formula and recover it
 * 
 * @author Anto Nanah Ji
 *
 */
public class CTLError {
	// Hash set of Java reserved words
	HashSet<String> reservedWordsSet = new HashSet<>();
	// Hash set of operators
	HashSet<String> operatorsSet = new HashSet<>();
	// Synchronized err and out prints
	ConsoleWriter cWriter = new ConsoleWriter();
	// number of input line
	static int inputLineNum = 0;
	// To verify if the fields exists in the user input
	boolean fieldNotExist = false;

	/**
	 * Inserts the Java reserved words into the reservedWordsSet Hash set. It also
	 * inserts the missing operators '&' and '|' into operatorsSet Hash set.
	 */
	public CTLError() {
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
		inputLineNum++; // input line number
		StringBuilder result = new StringBuilder(); // the recovered input
		String inputString = input.toString();
		String[] lines = inputString.split(" ");
		int size = lines.length;
		int index = 0; // the error character index in the input string
		boolean hasError = false;
		//cWriter.printlnout("Initial input: " + inputString);

		for (int i = 0; i < size; i++) {

			// check and recover if the input formula missing the operators '&' or '|'
			if (operatorsSet.contains(lines[i])) {
				hasError = true;
				// call underLineError method to print the error message
				underLineError(inputString, index, " token recognition error at: '" + lines[i] + " '");

				result.append(lines[i]);
			}
			// check and recover if the input formula contains any Java reserve word
			if (lines[i].contains(".")) {

				String[] substrings = lines[i].split("[.]");
				//FieldExists(lines[i], inputString, index);

				for (int j = 0; j < substrings.length; j++) {

					if (reservedWordsSet.contains(substrings[j])) {
						hasError = true;
						// call underLineError method to print the error message
						underLineError(inputString, index, " token recognition error at: '" + substrings[j] + " '");
						substrings[j] = substrings[j].toUpperCase();
					}
					result.append(substrings[j]);
					if (j < substrings.length - 1) {
						result.append(".");
					}
					index += substrings[j].length() + 1;
				}

			} else {
				result.append(lines[i]);
				index += lines[i].length() + 1;
			}
			result.append(" ");
		}

		// if there is field not found error then terminate
		if (fieldNotExist) {
			System.exit(1);
		}

		// if there is error return the recovered input
		if (hasError) {
			cWriter.printout("\nRecovered input: " + result.toString());
			return CharStreams.fromString(result.toString());
		}

		return input;
	}

	/**
	 * This method prints the error messages on the console. It also underlines the
	 * error location in the input.
	 * 
	 * @param errorLine          - input formula that contains error.
	 * @param charPositionInLine - error location in the input.
	 * @param errorChar          - reserved word or operator used in the input.
	 */
	private void underLineError(String errorLine, int charPositionInLine, String errorMsg) {
		cWriter.printerr("\nline " + inputLineNum + ":" + (charPositionInLine + 1) + errorMsg);
		cWriter.printerr("\n" + errorLine);
		cWriter.printerr("\n");
		// To underlines the error location
		for (int i = 0; i < charPositionInLine; i++)
			cWriter.printerr(" ");

		cWriter.printerr("^");
	}
}
