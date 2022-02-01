package labels;

import java.lang.reflect.Method;
import error.LabelReflectionException;
import gov.nasa.jpf.vm.Types;

public class InvokedMethod extends BinaryLabel {

	private static final String label_suffix = ".method";
	private final String parameterList, JNIName;

	public InvokedMethod(String qualifiedName, String parameterList, String path) {
		super(InvokedMethod.class.getSimpleName(), qualifiedName);
		
		// Build String Parameters
		this.parameterList = parameterList;
		
		// Identify Index of last dot
		int idx = qualifiedName.lastIndexOf('.');
		if (idx == -1) {
			throw new LabelReflectionException("the qualified name does not appear to contain both a class name and field name separated by a \'.\' character " + qualifiedName);
		}
		
		// Split qualifiedName into className and methodName
		String className = qualifiedName.substring(0, idx);
		String methodName = qualifiedName.substring(idx + 1);
		
		// Extract Parameter Types
		Class<?>[] parameterTypes = Utils.extractParameterTypes(parameterList, path);
		if (parameterTypes == null) {
			throw new LabelReflectionException("there was a problem reflecting parameter types " + parameterList);
		}
		
		// Build Class object
		Class<?> clazz = Utils.extractClass(className, path);
		if (clazz == null) {
			throw new LabelReflectionException("there was a problem reflecting class " + className);
		}
		
		// Extract method object
		Method method = Utils.extractMethod(clazz, methodName, parameterTypes);
		if (method == null) {
			throw new LabelReflectionException("there was a problem reflecting method " + methodName);
		}
		
		this.JNIName = className.replace('.', '_') + "_" + Types.getJNIMangledMethodName(method);
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
		return this.qualifiedName + parameterList;
	}

	@Override
	public String toString() {
		return this.name + " " + this.labelVal();
	}
}
