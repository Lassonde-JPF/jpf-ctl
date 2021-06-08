package errors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;

import parser.CTLLexer;

public class MyLexer extends CTLLexer {

	
	public MyLexer(CharStream input) {
		super(input);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void recover(LexerNoViableAltException e) {
		// TODO Auto-generated method stub
		
			String newFormula = "true";
			this.setText(newFormula);
			System.out.print("LPLP"+this.getText().toString());
		
	}

}
