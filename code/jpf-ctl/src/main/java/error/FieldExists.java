package error;

import parser.CTLBaseListener;
import parser.CTLParser;

import java.util.logging.*;

public class FieldExists extends CTLBaseListener {
	@Override
	public void enterAtomicProposition(CTLParser.AtomicPropositionContext ctx) {
		Logger logger = Logger.getLogger(FieldExists.class.getName());
		String atomicProposition = ctx.getText();
		int indexOfLastDot = atomicProposition.lastIndexOf(".");
		String className = atomicProposition.substring(0, indexOfLastDot);
		String fieldName = atomicProposition.substring(indexOfLastDot + 1);

		try {
			Class.forName(className).getDeclaredField(fieldName);
		} catch (ClassNotFoundException e) {
			logger.warning(" Class '" + className + " ' cannot be found\n");
			
		} catch (NoSuchFieldException | SecurityException e) {
			logger.warning(" Field '" + fieldName + " ' cannot be found\n");
		}
	}
}
