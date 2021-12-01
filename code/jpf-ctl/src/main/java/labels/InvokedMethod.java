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
		
		// Split qualifiedName into className and methodName
		String className = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
		String methodName = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
		
		// Build list of method parameter type objects
		Class<?>[] parameterTypes = Utils.extractParameterTypes(parameterList, path);
		if (parameterTypes == null) {
			throw new LabelReflectionException("there was a problem reflecting parameter types " + parameterList);
		}
		
		// Build Class object
		Class<?> clazz = Utils.extractClass(className, path);
		if (clazz == null) {
			throw new LabelReflectionException("there was a problem reflecting class " + className);
		}
		
		// Build Method Object
		Method method = Utils.extractMethod(clazz, methodName, parameterTypes);
		if (method == null) {
			throw new LabelReflectionException("there was a problem reflecting method " + methodName);
		}
		
		this.JNIName =  "invoked__" + className.replace('.', '_') + "_" + Types.getJNIMangledMethodName(method);
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
		return this.JNIName;
	}
}
