package labels;

import java.lang.reflect.Method;

import error.LabelReflectionException;
import gov.nasa.jpf.vm.Types;

public class BooleanLocalVariable extends BinaryLabel {

	private static final String label_suffix = ".variable";
	private final String parameterList, variableName, JNIName;

	public BooleanLocalVariable(String qualifiedName, String parameterList, String variableName, String path) {
		super(BooleanLocalVariable.class.getSimpleName(), qualifiedName);

		// Build String Parameters
		this.parameterList = parameterList;
		this.variableName = variableName;

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
