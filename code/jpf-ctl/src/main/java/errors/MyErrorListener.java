package errors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;

import parser.CTLParser;

public class MyErrorListener extends BaseErrorListener {
	public static boolean hadReservedWordsError = false;
	public static String usedReservedWord = "";
	
	HashSet<String> set = new HashSet<>();

	public MyErrorListener() {
		set.add("abstract");
		set.add("assert");
		set.add("boolean");
		set.add("break");
		set.add("byte");
		set.add("case");
		set.add("catch");
		set.add("char");
		set.add("class");
		set.add("const");
		set.add("continue");
		set.add("default");
		set.add("do");
		set.add("double");
		set.add("else");
		set.add("enum");
		set.add("extends");
		set.add("final");
		set.add("finally");
		set.add("float");
		set.add("for");
		set.add("if");
		set.add("goto");
		set.add("implements");
		set.add("import");
		set.add("instanceof");
		set.add("int");
		set.add("interface");
		set.add("long");
		set.add("native");
		set.add("new");
		set.add("package");
		set.add("private");
		set.add("protected");
		set.add("public");
		set.add("return");
		set.add("short");
		set.add("static");
		set.add("strictfp");
		set.add("super");
		set.add("switch");
		set.add("synchronized");
		set.add("this");
		set.add("throw");
		set.add("throws");
		set.add("transient");
		set.add("try");
		set.add("void");
		set.add("volatile");
		set.add("while");
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {

		
		 /*List<String> stack = ((CTLParser) recognizer).getRuleInvocationStack();
		 Collections.reverse(stack); System.err.println("line" + line + ":" +
		 charPositionInLine + "at" + offendingSymbol + ":" + msg);
		 System.err.println("Syntax Error!"); System.err.println("Token " + "\"" +
		 ((Token) offendingSymbol).getText() + "\"" + " (line " + line + ", column " +
		 (charPositionInLine + 1) + ")" + ": " + msg);
		 
		 System.err.println("Rule Stack:" + stack);*/
		 
		//reservedWordsError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
		//System.err.println("line"+line+":"+charPositionInLine+" "+msg);
		underlineError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
	}

	private void reservedWordsError(Recognizer recognizer, Token offendingSymbol, int line, int charPositionInLine) {
		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		String[] lines = input.split(" ");
		int size = lines.length;
		int openBrackets = 0;
		int closeBrackets = 0;
		System.out.println("input: " + input);

		for (int i = 0; i < size; i++) {
			if (lines[i] == "(")
				openBrackets++;

			if (lines[i] == ")")
				closeBrackets++;
			if (lines[i].contains(".")) {
				String[] substrings = lines[i].split(".");
				for (int j = 0; j < substrings.length; j++) {
					if (set.contains(substrings[j])) {
						hadReservedWordsError = true;
						usedReservedWord = substrings[j];
					}
				}
			}
		}
		if (openBrackets > closeBrackets) {

			System.err.println("Syntax Error! Missing closing brackets!!!");
		} else if (openBrackets < closeBrackets) {
			System.err.println("Syntax Error! Missing opening brackets!!!");
		}
		if (hadReservedWordsError) {
			System.err.println("Syntax Error! " + usedReservedWord + " is a reserve word in the Java Language!!!");
		}

	}

	protected void underlineError(Recognizer recognizer, Token offendingToken, int line, int charPositionInLine)
	{
		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		String[] lines = input.split("\n");
		String errorLine=lines[line-1];
		System.err.println(errorLine);
		for(int i=0; i<charPositionInLine; i++)
			System.err.print(" ");
		
		
		int start = offendingToken.getStartIndex();
		int stop = offendingToken.getStopIndex();
		if(start>=0 && stop>=0)
		{
			for(int i=start; i<=stop; i++)
				System.err.print("^");
		}
		System.err.println();
		
	
	}
}
