package jpf.logic;

import java.util.Properties;

import gov.nasa.jpf.vm.Types;

/**
 * A label representing the return value of a boolean method.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class ReturnedBooleanMethod extends Label {
	private String name;
	private String parameters;
	private boolean value;
	
	public ReturnedBooleanMethod(String name, String parameters, boolean value) {
		this.name = name;
		this.parameters = parameters;
		this.value = value;
	}
	
	public String getLabelClass() {
		return "label.ReturnedBooleanMethod";
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.ReturnedBooleanMethod.method", this.name + this.parameters);
		return properties;
	}
	
	public String getMangledName() {
		return "" + this.value + "__" + Types.getJNIMangledMethodName(null, this.name, this.parameters);
	}
}
