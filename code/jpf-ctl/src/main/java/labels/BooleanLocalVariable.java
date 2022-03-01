package labels;

import java.lang.reflect.Method;

import error.LabelReflectionException;
import gov.nasa.jpf.vm.Types;

/**
 * BooleanLocalVariable - A label representing a boolean local variable
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class BooleanLocalVariable extends BinaryLabel {

	// Attributes
	private static final String label_suffix = ".variable";
	private final String parameterList, variableName, JNIName;
	private final boolean value;

	/**
	 * Initializes this BooleanLocalVariable with a qualified name, parameter list, variable name, and path.
	 * 
	 * @param qualifiedName - qualified name of the method containing this boolean local variable
	 * @param parameterList - parameter type list of the method containing this boolean local variable
	 * @param variableName - variable name of this boolean local variable
	 * @param path - classpath where this boolean local variable resides
	 */
	public BooleanLocalVariable(String qualifiedName, String parameterList, String variableName, String value, String path) {
		super(BooleanLocalVariable.class.getSimpleName(), qualifiedName);

		// Build String Parameters
		this.parameterList = parameterList;
		this.variableName = variableName;
		
		// Parse value (should be fine since ANTLR will not tokenize if it's not true or false exactly)
		this.value = Boolean.parseBoolean(value);

		// Identify Index of last dot
		int idx = qualifiedName.lastIndexOf('.');
		if (idx == -1) {
			throw new LabelReflectionException("the qualified name does not appear to contain both a class name and field name separated by a \'.\' character " + qualifiedName);
		}
		
		// Split qualifiedName into className and fieldName
		String className = qualifiedName.substring(0, idx);
		String methodName = qualifiedName.substring(idx + 1);

		// Extract Parameter Types
		Class<?>[] parameterTypes = Utils.extractParameterTypes(parameterList, path);
		if (parameterTypes == null) {
			throw new LabelReflectionException("there was a problem reflecting parameter types " + parameterList);
		}
		
		// Extract class object
		Class<?> clazz = Utils.extractClass(className, path);
		if (clazz == null) {
			throw new LabelReflectionException("there was a problem reflecting class " + className);
		}
		
		// Extract method object
		Method method = Utils.extractMethod(clazz, methodName, parameterTypes);
		if (method == null) {
			throw new LabelReflectionException("there was a problem reflecting method " + methodName);
		}
		
		this.JNIName = this.getQualifiedName().replace('.', '_') + "_" + Types.getJNIMangledMethodName(method);
	}

	public boolean getValue() {
		return this.value;
	}
	
	@Override
	public String getJNIName() {
		return this.JNIName;
	}

	@Override
	public String labelDef() {
		return label_prefix + this.name + label_suffix;
	}

	@Override
	public String labelVal() {
		return this.qualifiedName + this.parameterList + ":" + this.variableName;
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
			BooleanLocalVariable other = (BooleanLocalVariable) object;
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
