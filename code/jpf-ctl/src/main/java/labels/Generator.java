package labels;

import org.label.LabelBaseVisitor;
import org.label.LabelParser;

public class Generator extends LabelBaseVisitor<Label> {
	
	private String path;
	
	public Generator(String path) {
		this.path = path;
	}

	@Override
	public Label visitInitial(LabelParser.InitialContext ctx) {
		return new Initial();
	}

	@Override
	public Label visitEnd(LabelParser.EndContext ctx) {
		return new End();
	}

	@Override
	public Label visitBooleanStaticField(LabelParser.BooleanStaticFieldContext ctx) {
		return new BooleanStaticField(ctx.referenceType().getText(), path);
	}

	@Override
	public Label visitBooleanLocalVariable(LabelParser.BooleanLocalVariableContext ctx) {
		return new BooleanLocalVariable(ctx.referenceType().getText(), ctx.parameters().getText(), ctx.variableType().getText(), path);
	}

	@Override
	public Label visitInvokedMethod(LabelParser.InvokedMethodContext ctx) {
		return new InvokedMethod(ctx.referenceType().getText(), ctx.parameters().getText(), path);
	}

	@Override
	public Label visitReturnedVoidMethod(LabelParser.ReturnedVoidMethodContext ctx) {
		return new ReturnedVoidMethod(ctx.referenceType().getText(), ctx.parameters().getText(), path);
	}

	@Override
	public Label visitReturnedBooleanMethod(LabelParser.ReturnedBooleanMethodContext ctx) {
		return new ReturnedBooleanMethod(ctx.referenceType().getText(), ctx.parameters().getText(), path);
	}

	@Override
	public Label visitThrownException(LabelParser.ThrownExceptionContext ctx) {
		return new ThrownException(ctx.referenceType().getText(), path);
	}

	@Override
	public Label visitSynchronizedStaticMethod(LabelParser.SynchronizedStaticMethodContext ctx) {
		return new SynchronizedStaticMethod(ctx.referenceType().getText(), ctx.parameters().getText(), path);
	}
	
}
