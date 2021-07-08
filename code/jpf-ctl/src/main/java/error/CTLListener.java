package error;

import org.ctl.CTLBaseListener;
import org.ctl.CTLParser.AtomicPropositionContext;

public class CTLListener extends CTLBaseListener {

	@Override
	public void enterAtomicProposition(AtomicPropositionContext ctx) {
		// TODO Auto-generated method stub
		// System.out.println(ctx.getText());
	}

	@Override
	public void exitAtomicProposition(AtomicPropositionContext ctx) {
		// TODO Auto-generated method stub
	}

}
