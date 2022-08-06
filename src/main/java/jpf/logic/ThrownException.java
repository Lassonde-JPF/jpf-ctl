package jpf.logic;

import java.util.Properties;

/**
 * A label representing a thrown exception.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class ThrownException extends Label {
	private String name;

	public ThrownException(String name) {
		this.name = name;
	}

	public String getLabelClass() {
		return "label.ThrownException";
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.ThrownException.exception", this.name);
		return properties;
	}
	
	public String getMangledName() {
		return "thrown__" + this.name.replaceAll("[$.]", "_");
	}
}
