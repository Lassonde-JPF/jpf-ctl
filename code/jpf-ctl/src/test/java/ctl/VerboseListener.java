package ctl;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import parser.CTLParser;



public class VerboseListener extends BaseErrorListener {
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, 
			 				Object offendingSymbol, 
			 				int line, int charPositionInLine,
			 				String msg, RecognitionException e) {
		
		List<String> stack= ((CTLParser)recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		System.err.println("rulestack:"+stack);
		System.err.println("line"+line+":"+charPositionInLine+"at"+offendingSymbol+":"+msg);
	}
	

}
