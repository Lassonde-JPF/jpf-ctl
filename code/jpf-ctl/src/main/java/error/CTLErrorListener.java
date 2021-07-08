package error;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class CTLErrorListener extends BaseErrorListener {

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {

		/*
		 * List<String> stack = ((CTLParser) recognizer).getRuleInvocationStack();
		 * Collections.reverse(stack); System.err.println("line" + line + ":" +
		 * charPositionInLine + "at" + offendingSymbol + ":" + msg);
		 * System.err.println("Syntax Error!"); System.err.println("Token " + "\"" +
		 * ((Token) offendingSymbol).getText() + "\"" + " (line " + line + ", column " +
		 * (charPositionInLine + 1) + ")" + ": " + msg);
		 * 
		 * System.err.println("Rule Stack:" + stack);
		 */
		System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
		underlineError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
	}

	protected void underlineError(Recognizer<?, ?> recognizer, Token offendingToken, int line, int charPositionInLine) {
		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		String[] lines = input.split("/n");
		String errorLine = lines[line - 1];
		System.err.println(errorLine);

		for (int i = 0; i < charPositionInLine; i++)
			System.err.print(" ");

		int start = offendingToken.getStartIndex();
		int stop = offendingToken.getStopIndex();
		if (start >= 0 && stop >= 0) {
			for (int i = start; i <= stop; i++)
				System.err.print("^");
		}
		System.err.println();

	}
}
