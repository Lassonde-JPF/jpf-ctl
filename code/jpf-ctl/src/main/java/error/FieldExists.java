package error;

import org.ctl.CTLBaseListener;
import org.ctl.CTLParser;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

public class FieldExists extends CTLBaseListener {
	
	private final Set<String> APs = new HashSet<String>();

	@Override
	public void enterAtomicProposition(CTLParser.AtomicPropositionContext ctx) {
		Logger logger = Logger.getLogger(FieldExists.class.getName());
		String atomicProposition = ctx.getText();
		int indexOfLastDot = atomicProposition.lastIndexOf(".");
		String className = atomicProposition.substring(0, indexOfLastDot);
		String fieldName = atomicProposition.substring(indexOfLastDot + 1);

		try {
			Class.forName(className).getDeclaredField(fieldName);
			
			APs.add(ctx.getText());
		} catch (ClassNotFoundException e) {
			logger.warning(" Class '" + className + " ' cannot be found\n");
			
		} catch (NoSuchFieldException | SecurityException e) {
			logger.warning(" Field '" + fieldName + " ' cannot be found\n");
		}
	}

	public Set<String> getAPs() {
		return this.APs;
	}
}
