package errors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import parser.CTLParser;

public class MyErrorListener extends BaseErrorListener {
	public static boolean hadReservedWordsError = false;
	private HashSet<String> set = new HashSet<>();
	
	public MyErrorListener()
	{
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
		Collections.reverse(stack);
		System.err.println("line" + line + ":" + charPositionInLine + "at" + offendingSymbol + ":" + msg);
		System.err.println("Syntax Error!");
		System.err.println("Token " + "\"" + ((Token) offendingSymbol).getText() + "\""
						   +
						   " (line " + line + ", column " + (charPositionInLine + 1) + ")"
						   +
						   ": " + msg);
		System.err.println("Rule Stack:" + stack);*/
		reservedWordsError(recognizer,(Token) offendingSymbol,  line,   charPositionInLine);
	}
	
	protected void reservedWordsError (Recognizer recognizer,
								  Token offerndingToken, int line,
								   int charPositionInLine) 
	{
		// TODO Auto-generated method stub
		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		String[] lines = input.split(" ");
		int size = lines.length;
		int openBrackets = 0;
		int closeBrackets = 0;
		String errorReservedWord = "";
		
		

		
		for(int i = 0; i < size; i++)
		{
			if(lines[i] == "(")
				openBrackets++;
			
			if(lines[i] == ")")
				closeBrackets++;
			
			if(set.contains(lines[i]))
			{
				hadReservedWordsError = true;
				errorReservedWord = lines[i];
			}
					
		}
		
		if(hadReservedWordsError)
		{
			System.err.println("Syntax Error! " + errorReservedWord + " is a reserve word in the Java Language!!!");
		}
		
		if(openBrackets > closeBrackets)
		{
			
			System.err.println("Syntax Error! Missing closing brackets!!!");
		}
		else if(openBrackets < closeBrackets)
		{
			System.err.println("Syntax Error! Missing opening brackets!!!");
		}
	}
	

}
