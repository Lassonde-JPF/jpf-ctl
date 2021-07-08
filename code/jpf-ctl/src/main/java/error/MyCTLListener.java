package error;

import parser.CTLBaseListener;
import parser.CTLParser.AtomicPropositionContext;

public class MyCTLListener  extends CTLBaseListener{
	
	@Override
	public void enterAtomicProposition(AtomicPropositionContext ctx) {
		// TODO Auto-generated method stub
		//System.out.println(ctx.getText());
	}

	@Override
	public void exitAtomicProposition(AtomicPropositionContext ctx) {
		// TODO Auto-generated method stub
	}

}
