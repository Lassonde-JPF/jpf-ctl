package error;

import org.ctl.CTLBaseListener;
import org.ctl.CTLParser;

import java.util.HashSet;
import java.util.Set;

public class FieldExists extends CTLBaseListener {
	
	public static final Set<String> APs = new HashSet<String>();

	@Override
	public void enterAtomicProposition(CTLParser.AtomicPropositionContext ctx) {
		String atomicProposition = ctx.getText();
		int indexOfLastDot = atomicProposition.lastIndexOf(".");
		String className = atomicProposition.substring(0, indexOfLastDot);
		String fieldName = atomicProposition.substring(indexOfLastDot + 1);

		try {
			Class.forName(className).getDeclaredField(fieldName);
			APs.add(ctx.getText());
		} catch (ClassNotFoundException e) {
			throw new AtomicPropositionDoesNotExistException(" Class '" + className + " ' cannot be found\n");
		} catch (NoSuchFieldException | SecurityException e) {
			throw new AtomicPropositionDoesNotExistException(" Field '" + fieldName + " ' cannot be found\n");
		}
	}
}
