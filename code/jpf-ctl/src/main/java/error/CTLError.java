package error;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

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
	//HashSet<String> reservedWordsSet = new HashSet<>();
	// Hash set of operators
	HashSet<String> operatorsSet = new HashSet<>();
	// number of input line
	static int inputLineNum = 0;

	StringBuilder errMsg = new StringBuilder();

	/**
	 * Inserts the Java reserved words into the reservedWordsSet Hash set. It also
	 * inserts the missing operators '&' and '|' into operatorsSet Hash set.
	 */
	public CTLError() {
		// adding Java reserve words to the hash set
		/*reservedWordsSet.addAll(Arrays.asList(new String[] { "abstract", "assert", "boolean", "break", "byte", "case",
				"catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends",
				"final", "finally", "float", "for", "if", "goto", "implements", "import", "instanceof", "int",
				"interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short",
				"static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
				"void", "volatile", "while" }));
		*/
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

		inputLineNum++;									//input line number
		StringBuilder recovedInput = new StringBuilder();		//the recovered input
		String inputString = input.toString();
		String[] lines = inputString.split(" ");
		int size = lines.length;
		int index = 0;									//the error character index in the input string
		boolean hasError = false;

		
		for (int i = 0; i < size; i++) {
			
			//check and recover if the input formula missing the operators '&' or '|'
			if (operatorsSet.contains(lines[i])) {
				hasError = true;
				//call underLineError method to print the error message
				underLineError(inputString, index, " token recognition error at: '"+ lines[i] + " '" );
				index += 2;
				recovedInput.append(lines[i]);	
				recovedInput.append(lines[i]);
			}
			/*//check and recover if the input formula contains any Java reserve word
			if (lines[i].contains(".")) {
				
				//check if the fields in the input exist
				//FieldExists(lines[i],inputString, index);
				
				String[] substrings = lines[i].split("[.]");
			
				for (int j = 0; j < substrings.length; j++) {

					if (reservedWordsSet.contains(substrings[j])) {
						hasError = true;
						//call underLineError method to print the error message 
						underLineError(inputString, index," token recognition error at: '"+ substrings[j] + " '" );
						substrings[j] = substrings[j].toUpperCase();
					}
					recovedInput.append(substrings[j]);
					if (j < substrings.length - 1)
					{
						recovedInput.append(".");							
					}
					index += substrings[j].length() + 1;					
				}
				
				
			}*/
			 else 
			{				
				recovedInput.append(lines[i]);	
				index += lines[i].length() + 1;
			}			
			recovedInput.append(" ");			
		}	

		//if there is error print message on the console and return the recovered input
		if(hasError)
		{
			System.err.print(errMsg.toString() );
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				
			}
			System.out.print("Initial   input: " + inputString + "\n" + "Recovered input: " + recovedInput.toString() + "\n\n");
			
			return CharStreams.fromString(recovedInput.toString());
		}
		
		return input;		
	}
	
	/**
	 * This method prints the error messages on the console.
	 * It also underlines the error location in the input.
	 * 
	 * @param errorLine	- input formula that contains error.
	 * @param charPositionInLine - error location in the input.
	 * @param errorMsg - error message.
	 */
	private void underLineError(String errorLine, int charPositionInLine, String errorMsg )
	{
		
		errMsg.append("\nline "+ inputLineNum +":"+ (charPositionInLine + 1) + errorMsg + "\n");
		errMsg.append(errorLine+ "\n");
		//To underlines the error location
		for(int i=0; i<charPositionInLine; i++)
			errMsg.append(" ");
		
		errMsg.append("^"+ "\n");
	}
}

