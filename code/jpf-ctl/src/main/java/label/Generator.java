package label;

import org.label.LabelBaseVisitor;
import org.label.LabelParser;

public class Generator extends LabelBaseVisitor<Label> {

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
		return new BooleanStaticField(ctx.QUALIFIEDNAME().getText());
	}

	@Override
	public Label visitIntegerStaticField(LabelParser.IntegerStaticFieldContext ctx) {
		return new IntegerStaticField(ctx.QUALIFIEDNAME().getText());
	}

	@Override
	public Label visitBooleanLocalVariable(LabelParser.BooleanLocalVariableContext ctx) {
		return new BooleanLocalVariable(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText(), ctx.IDENTIFIER().getText());
	}

	@Override
	public Label visitIntegerLocalVariable(LabelParser.IntegerLocalVariableContext ctx) {
		return new IntegerLocalVariable(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText(), ctx.IDENTIFIER().getText());
	}

	@Override
	public Label visitInvokedMethod(LabelParser.InvokedMethodContext ctx) {
		return new InvokedMethod(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText());
	}

	@Override
	public Label visitReturnedVoidMethod(LabelParser.ReturnedVoidMethodContext ctx) {
		return new ReturnedVoidMethod(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText());
	}

	@Override
	public Label visitReturnedBooleanMethod(LabelParser.ReturnedBooleanMethodContext ctx) {
		return new ReturnedBooleanMethod(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText());
	}

	@Override
	public Label visitReturnedIntegerMethod(LabelParser.ReturnedIntegerMethodContext ctx) {
		return new ReturnedIntegerMethod(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText());
	}

	@Override
	public Label visitThrownException(LabelParser.ThrownExceptionContext ctx) {
		return new ThrownException(ctx.QUALIFIEDNAME().getText());
	}

	@Override
	public Label visitSynchronizedStaticMethod(LabelParser.SynchronizedStaticMethodContext ctx) {
		return new SynchronizedStaticMethod(ctx.QUALIFIEDNAME().getText(), ctx.PARAMETERS().getText());
	}
}
