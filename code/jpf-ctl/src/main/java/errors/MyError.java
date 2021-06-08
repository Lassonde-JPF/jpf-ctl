package errors;

import java.util.HashSet;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

public class MyError {

	HashSet<String> reservedWordsSet = new HashSet<>();
	HashSet<String> operatorsSet = new HashSet<>();

	public MyError() {
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

	public CharStream errorCheckAndRecover(CharStream input) {
		StringBuilder result = new StringBuilder();
		String inputString = input.toString();
		String[] lines = inputString.split(" ");
		int size = lines.length;
		boolean hasError = false;
		System.out.println("Initial input: " + inputString);

		for (int i = 0; i < size; i++) {
			//check and recover if the input formula missing the operators '&' or '|'
			if (operatorsSet.contains(lines[i])) {
				
				System.err.println(
						"Syntax Error! " + lines[i] + " is a wrong operator!!!");
						
				result.append(lines[i]);	
			}
			//check and recover if the input formula contains any Java reserve word
			if (lines[i].contains(".")) {

				String[] substrings = lines[i].split("[.]");

				for (int j = 0; j < substrings.length; j++) {

					if (reservedWordsSet.contains(substrings[j])) {
						hasError = true;
						System.err.println(
								"Syntax Error! " + substrings[j] + " is a reserve word in the Java Language!!!");
						substrings[j] = substrings[j].toUpperCase();
					}
					result.append(substrings[j]);
					if (j == 0)
						result.append(".");
				}

			} else {
				result.append(lines[i]);		
			}
			result.append(" ");
		}		
		if(hasError)
		{
			System.out.println("Recovered input: " + result.toString());
		}
		
		return CharStreams.fromString(result.toString());
	}

}
