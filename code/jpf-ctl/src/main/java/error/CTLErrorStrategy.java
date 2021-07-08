package error;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

public class CTLErrorStrategy extends DefaultErrorStrategy {

	@Override
	public void recover(Parser recognizer, RecognitionException e) {
		// TODO Auto-generated method stub
		// throw new RuntimeException(e);
	}
}
