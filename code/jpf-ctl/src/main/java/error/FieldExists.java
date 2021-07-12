package error;

import org.ctl.CTLBaseListener;
import org.ctl.CTLParser;

public class FieldExists extends CTLBaseListener {

	@Override
	public void enterAtomicProposition(CTLParser.AtomicPropositionContext ctx) {
		String atomicProposition = ctx.getText();
		int indexOfLastDot = atomicProposition.lastIndexOf(".");
		String className = atomicProposition.substring(0, indexOfLastDot);
		String fieldName = atomicProposition.substring(indexOfLastDot + 1);

		try {
			Class.forName(className).getDeclaredField(fieldName);
		} catch (ClassNotFoundException e) {
			System.err.println("WARNING: class " + className + " does not exist");
		} catch (NoSuchFieldException | SecurityException e) {
			System.err.println("WARNING: field " + fieldName + " does not exist");
		}
	}

}
