package jpf.logic;

import java.util.Properties;

/**
 * A label representing a method invocation.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class InvokedMethod extends Label {
	private String name;
	private String parameters;

	public InvokedMethod(String methodName, String parameters) {
		this.name = methodName;
		this.parameters = parameters;
	}
	
	public String getLabelClass() {
		return "label.InvokedMethod";
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.InvokedMethod.method", this.name + this.parameters);
		return properties;
	}
	
	public String getMangledName() {
		try {
			return "\"invoked__" + getMangledName(this.name, this.parameters) + "\"";
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return "NO NAME";
		}
	}
}
