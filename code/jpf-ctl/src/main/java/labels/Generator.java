package labels;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
		return new BooleanStaticField(ctx.referenceType().getText());
	}

	@Override
	public Label visitIntegerStaticField(LabelParser.IntegerStaticFieldContext ctx) {
		return new IntegerStaticField(ctx.referenceType().getText());
	}

	@Override
	public Label visitBooleanLocalVariable(LabelParser.BooleanLocalVariableContext ctx) {
		return new BooleanLocalVariable(ctx.referenceType().getText(), ctx.parameters().getText(), ctx.variableType().getText());
	}

	@Override
	public Label visitIntegerLocalVariable(LabelParser.IntegerLocalVariableContext ctx) {
		return new IntegerLocalVariable(ctx.referenceType().getText(), ctx.parameters().getText(), ctx.variableType().getText());
	}

	@Override
	public Label visitInvokedMethod(LabelParser.InvokedMethodContext ctx) {
		return new InvokedMethod(ctx.referenceType().getText(), ctx.parameters().getText());
	}

	@Override
	public Label visitReturnedVoidMethod(LabelParser.ReturnedVoidMethodContext ctx) {
		return new ReturnedVoidMethod(ctx.referenceType().getText(), ctx.parameters().getText());
	}

	@Override
	public Label visitReturnedBooleanMethod(LabelParser.ReturnedBooleanMethodContext ctx) {
		return new ReturnedBooleanMethod(ctx.referenceType().getText(), ctx.parameters().getText());
	}

	@Override
	public Label visitReturnedIntegerMethod(LabelParser.ReturnedIntegerMethodContext ctx) {
		return new ReturnedIntegerMethod(ctx.referenceType().getText(), ctx.parameters().getText());
	}

	@Override
	public Label visitThrownException(LabelParser.ThrownExceptionContext ctx) {
		return new ThrownException(ctx.referenceType().getText());
	}

	@Override
	public Label visitSynchronizedStaticMethod(LabelParser.SynchronizedStaticMethodContext ctx) {
		return new SynchronizedStaticMethod(ctx.referenceType().getText(), ctx.parameters().getText());
	}
	
	
	@SuppressWarnings("unused")
	private List<String> extractParameters(String parameterString) {
		return Pattern.compile(",").splitAsStream(parameterString).collect(Collectors.toList());
	}
	
}
