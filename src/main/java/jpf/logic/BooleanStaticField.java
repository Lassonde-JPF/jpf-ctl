package jpf.logic;

import java.util.Properties;

/**
 * A label representing a boolean static field.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class BooleanStaticField extends Label {
	private String name;
	private boolean value;
	
	public BooleanStaticField(String name, boolean value) {
		this.name = name;
		this.value = value;
	}

	public String getLabelClass() {
		return "label.BooleanStaticField";
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put("label.BooleanStaticField.field", this.name);
		return properties;
	}
	
	public String getMangledName() {
		return "\"" + this.value + "__" + this.name.replaceAll("[$.]", "_") + "\"";
	}
}
