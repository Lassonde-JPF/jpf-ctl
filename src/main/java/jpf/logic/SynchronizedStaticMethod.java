package jpf.logic;

import java.util.Properties;

import gov.nasa.jpf.vm.Types;

/**
 * A label representing a synchronized static method.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class SynchronizedStaticMethod extends Label {
	private String methodName;
	private String parameters;
	
	public SynchronizedStaticMethod(String methodName, String parameters) {
		this.methodName = methodName;
		this.parameters = parameters;
	}
	
	public String getLabelClass() {
		return "label.SynchronizedStaticMethod";
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.SynchronizedStaticMethod.method", this.methodName + this.parameters);
		return properties;
	}
	
	public String getMangledName() {
		return "(un)?locked__" + Types.getJNIMangledMethodName(null, this.methodName, this.parameters);
	}
}
