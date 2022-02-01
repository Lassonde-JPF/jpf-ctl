package labels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class LabelTest extends BaseTest {

	// TODO Enable when random works
	@Disabled
	@RepeatedTest(BaseTest.CASES)
	public void testRandomLabel() {
		Label expected = Label.random();
		ParseTree tree = parse(expected.toString());
		Label actual = generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testInitial() {
		Initial expected = new Initial();
		ParseTree tree = parse(expected.toString());
		Initial actual = (Initial) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEnd() {
		End expected = new End();
		ParseTree tree = parse(expected.toString());
		End actual = (End) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBooleanLocalVariable() {
		String qualifiedName = "labels.ReflectionExamples.main";
		String parameterList = "(java.lang.String[])";
		String variableName = "variable";
		
		BooleanLocalVariable expected = new BooleanLocalVariable(qualifiedName, parameterList, variableName, PATH);
		ParseTree tree = parse(expected.toString());
		BooleanLocalVariable actual = (BooleanLocalVariable) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBooleanStaticField() {
		String qualifiedName = "labels.ReflectionExamples.one";
		
		BooleanStaticField expected = new BooleanStaticField(qualifiedName, PATH);
		ParseTree tree = parse(expected.toString());
		BooleanStaticField actual = (BooleanStaticField) generator.visit(tree); 
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}
	
	@Test
	public void testInvokedMethod() {
		String qualifiedName = "labels.ReflectionExamples.setValue";
		String parameterList = "(boolean)";
		
		InvokedMethod expected = new InvokedMethod(qualifiedName, parameterList, PATH);
		ParseTree tree = parse(expected.toString());
		InvokedMethod actual = (InvokedMethod) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}
	
	@Test
	public void testReturnedBooleanMethod() {
		String qualifiedName = "labels.ReflectionExamples.getRandom";
		String parameterList = "()";
		
		ReturnedBooleanMethod expected = new ReturnedBooleanMethod(qualifiedName, parameterList, PATH);
		ParseTree tree = parse(expected.toString());
		ReturnedBooleanMethod actual = (ReturnedBooleanMethod) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}
	
	@Test
	public void testReturnedVoidMethod() {
		String qualifiedName = "labels.ReflectionExamples.setValue";
		String parameterList = "(boolean)";
		
		ReturnedVoidMethod expected = new ReturnedVoidMethod(qualifiedName, parameterList, PATH);
		ParseTree tree = parse(expected.toString());
		ReturnedVoidMethod actual = (ReturnedVoidMethod) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}
	
	@Test
	public void testSynchronizedStaticMethod() {
		String qualifiedName = "labels.ReflectionExamples.setValueSynchronized";
		String parameterList = "(boolean)";
		
		SynchronizedStaticMethod expected = new SynchronizedStaticMethod(qualifiedName, parameterList, PATH);
		ParseTree tree = parse(expected.toString());
		SynchronizedStaticMethod actual = (SynchronizedStaticMethod) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}
	
	@Test
	public void testThrownExeception() {
		String qualifiedName = "java.util.zip.DataFormatException";
		
		ThrownException expected = new ThrownException(qualifiedName, PATH);
		ParseTree tree = parse(expected.toString());
		ThrownException actual = (ThrownException) generator.visit(tree);
		
		assertNotNull(actual, tree.toString());
		assertEquals(expected, actual, expected.toString() + "\n" + actual.toString());
	}
	
	
}
