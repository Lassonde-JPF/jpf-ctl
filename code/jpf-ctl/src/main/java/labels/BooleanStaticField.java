package labels;

import java.lang.reflect.Field;

import error.LabelReflectionException;

/**
 * BooleanStaticField - A label representing a boolean static field
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class BooleanStaticField extends BinaryLabel {

	// Attributes
	private static final String label_suffix = ".field";
	private final String JNIName;
	
	/**
	 * Initializes this BooleanStaticField with a qualified name and path
	 * @param qualifiedName - qualified name of the method containing this boolean static field
	 * @param path - classpath where this boolean local variable resides
	 */
	public BooleanStaticField(String qualifiedName, String path) {
		super(BooleanStaticField.class.getSimpleName(), qualifiedName);
		
		// Identify Index of last dot
		int idx = qualifiedName.lastIndexOf('.');
		if (idx == -1) {
			throw new LabelReflectionException("the qualified name does not appear to contain both a class name and field name separated by a \'.\' character " + qualifiedName);
		}
		
		// Split qualifiedName into className and fieldName
		String className = qualifiedName.substring(0, idx);
		String fieldName = qualifiedName.substring(idx + 1);
		
		// Extract class object
		Class<?> clazz = Utils.extractClass(className, path);
		if (clazz == null) {
			throw new LabelReflectionException("there was a problem reflecting class " + className);
		}
		
		// Extract field object
		Field field = Utils.extractField(clazz, fieldName);
		if (field == null) {
			throw new LabelReflectionException("there was a problem reflecting field " + fieldName);
		}
		
		this.JNIName = className.replace('.', '_') + "_" + field.getName();
	}
	
	@Override
	public String getJNIName() {
		return this.JNIName;
	}

	@Override
	public String labelDef() {
		return label_prefix + this.name + BooleanStaticField.label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * hashCode + this.name.hashCode();
		hashCode = prime * hashCode + this.qualifiedName.hashCode();
		hashCode = prime * hashCode + this.JNIName.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			BooleanStaticField other = (BooleanStaticField) object;
			return this.name.equals(other.name) && this.qualifiedName.equals(other.qualifiedName)
					&& this.JNIName.equals(other.JNIName);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.name + " " + this.labelVal();
	}

}
