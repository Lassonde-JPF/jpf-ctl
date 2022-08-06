package jpf.logic;

import java.util.Properties;

/**
 * A label representing that a void method returned.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class ReturnedVoidMethod extends Label {
	private String name;
	private String parameters;

	public ReturnedVoidMethod(String name, String parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	public String getLabelClass() {
		return "label.ReturnedVoidMethod";
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.ReturnedVoidMethod.method", this.name + this.parameters);
		return properties;
	}

	public String getMangledName() {
		try {
			return "\"returned__" + getMangledName(this.name, this.parameters) + "\"";
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return "NO NAME";
		}
	}
}
