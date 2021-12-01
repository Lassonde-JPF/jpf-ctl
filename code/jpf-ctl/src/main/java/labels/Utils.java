package labels;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gov.nasa.jpf.vm.Types;

public class Utils {

	public static Class<?>[] extractParameterTypes(String parameterList, String classPath) {
		String innerParameters = parameterList.substring(parameterList.indexOf('(') + 1, parameterList.indexOf(')'));
		List<Class<?>> typeObjects = Pattern.compile(",").splitAsStream(innerParameters)
				.map(parameterType -> Types.getTypeSignature(parameterType, true)).map(typeSignature -> {
					try {
						return Class.forName(typeSignature);
					} catch (ClassNotFoundException e) {
						URLClassLoader cl;
						try {
							cl = new URLClassLoader(new URL[] { new File(classPath).toURI().toURL() });
						} catch (MalformedURLException e2) {
							cl = null;
						}
						try {
							return Class.forName(typeSignature, true, cl);
						} catch (ClassNotFoundException e1) {
							return null;
						}
					}
				}).collect(Collectors.toList());
		return typeObjects.toArray(new Class<?>[typeObjects.size()]);
	}
	
	public static Class<?> extractClass(String className, String classPath) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			URLClassLoader cl = null;
			try {
				cl = new URLClassLoader(new URL[] { new File(classPath).toURI().toURL() });
			} catch (MalformedURLException e1) {
			}
			try {
				return Class.forName(className, true, cl);
			} catch (ClassNotFoundException e1) {
				return null;
			}
		}
	}
	
	public static Method extractMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		try {
			return clazz.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
}
