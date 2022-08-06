package jpf.logic;

import java.util.Properties;

import gov.nasa.jpf.vm.Types;

/**
 * A label representing a boolean local variable.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class BooleanLocalVariable extends Label {
	private String methodName;
	private String parameters;
	private String variableName;
	private boolean value;

	public BooleanLocalVariable(String methodName, String variableName, boolean value) {
		this.methodName = methodName;
		this.variableName = variableName;
		this.value = value;
	}
	
	public String getLabelClass() {
		return "label.BooleanLocalVariable";
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.BooleanLocalVariable.variable", this.methodName + this.parameters + ":" + this.variableName);
		return properties;
	}
	
	public String getMangledName() {
		return "" + this.value + "__" + Types.getJNIMangledMethodName(null, this.methodName, this.parameters) + "__" + this.variableName;
	}
}
