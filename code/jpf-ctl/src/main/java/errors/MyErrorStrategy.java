package errors;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import java.util.stream.IntStream;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

import org.antlr.v4.runtime.TokenStreamRewriter;

import parser.CTLParser;



public class MyErrorStrategy extends DefaultErrorStrategy {
	
	
	
	
	@Override
	public void recover(Parser recognizer, RecognitionException e) {
		// TODO Auto-generated method stub
		//throw new RuntimeException(e);

		
		if(MyErrorListener.hadReservedWordsError)
		{
			

			MyErrorListener.hadReservedWordsError = false;
			CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
			String input = tokens.getTokenSource().getInputStream().toString();
			String[] lines = input.split(" ");
			int size = lines.length;

			for(int i = 0; i < size; i++)
			{
				
				if(lines[i].equals(MyErrorListener.usedReservedWord))
				{
					lines[i] = "_" + lines[i];
				}
			}
	
			String newFormula = "( true )";
			//for(int i = 0; i < size; i++)
			//{
				//newInputString += lines[i];
			//}
			
			IntStream newInputStream = (IntStream) newFormula.chars();
			
			TokenStreamRewriter rewriter = new TokenStreamRewriter(recognizer.getTokenStream());
			rewriter.replace(0,"( true )");
			//recognizer.setInputStream((org.antlr.v4.runtime.IntStream) newFormula.chars());
			System.out.println(recognizer.getInputStream().getText());
			recognizer.setTokenStream(rewriter.getTokenStream());
			
			reset(recognizer);
			
			
		}
		
		
	}
	


}
