package error;

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

	/**
	 * Inserts the Java reserved words into the reservedWordsSet Hash set. It also
	 * inserts the missing operators '&' and '|' into operatorsSet Hash set.
	 */
	public CTLError() {
		// adding Java reserve words to the hash set
		reservedWordsSet.add("abstract");
		reservedWordsSet.add("assert");
		reservedWordsSet.add("boolean");
		reservedWordsSet.add("break");
		reservedWordsSet.add("byte");
		reservedWordsSet.add("case");
		reservedWordsSet.add("catch");
		reservedWordsSet.add("char");
		reservedWordsSet.add("class");
		reservedWordsSet.add("const");
		reservedWordsSet.add("continue");
		reservedWordsSet.add("default");
		reservedWordsSet.add("do");
		reservedWordsSet.add("double");
		reservedWordsSet.add("else");
		reservedWordsSet.add("enum");
		reservedWordsSet.add("extends");
		reservedWordsSet.add("final");
		reservedWordsSet.add("finally");
		reservedWordsSet.add("float");
		reservedWordsSet.add("for");
		reservedWordsSet.add("if");
		reservedWordsSet.add("goto");
		reservedWordsSet.add("implements");
		reservedWordsSet.add("import");
		reservedWordsSet.add("instanceof");
		reservedWordsSet.add("int");
		reservedWordsSet.add("interface");
		reservedWordsSet.add("long");
		reservedWordsSet.add("native");
		reservedWordsSet.add("new");
		reservedWordsSet.add("package");
		reservedWordsSet.add("private");
		reservedWordsSet.add("protected");
		reservedWordsSet.add("public");
		reservedWordsSet.add("return");
		reservedWordsSet.add("short");
		reservedWordsSet.add("static");
		reservedWordsSet.add("strictfp");
		reservedWordsSet.add("super");
		reservedWordsSet.add("switch");
		reservedWordsSet.add("synchronized");
		reservedWordsSet.add("this");
		reservedWordsSet.add("throw");
		reservedWordsSet.add("throws");
		reservedWordsSet.add("transient");
		reservedWordsSet.add("try");
		reservedWordsSet.add("void");
		reservedWordsSet.add("volatile");
		reservedWordsSet.add("while");

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
		cWriter.printlnout("Initial input: " + inputString);

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
				FieldExists(lines[i], inputString, index);

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

		// if there is error return the recovered input
		if (hasError) {
			cWriter.printlnout("Recovered input: " + result.toString());
			return CharStreams.fromString(result.toString());
		}

		return input;
	}

	private void FieldExists(String atomicProposition, String inputString, int errCharIndex) {
		int indexOfLastDot = atomicProposition.lastIndexOf(".");
		String className = atomicProposition.substring(0, indexOfLastDot);
		String fieldName = atomicProposition.substring(indexOfLastDot + 1);

		try {
			Class.forName(className).getDeclaredField(fieldName);

		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {

			underLineError(inputString, errCharIndex, " Class '" + className + " ' cannot be found");
			System.exit(1);
		}
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
		cWriter.printlnerr("line " + inputLineNum + ":" + (charPositionInLine + 1) + errorMsg);
		cWriter.printlnerr(errorLine);
		// To underlines the error location
		for (int i = 0; i < charPositionInLine; i++)
			cWriter.printerr(" ");

		cWriter.printlnerr("^");
	}
}
