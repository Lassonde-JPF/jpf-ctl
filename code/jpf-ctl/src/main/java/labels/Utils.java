package labels;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gov.nasa.jpf.vm.Types;

/**
 * Utils - Utilities for label reflection
 * 
 * @author mattw
 *
 */
public class Utils {

	/**
	 * Extracts parameter types from a given string representation of a parameter
	 * list on a particular classpath
	 * 
	 * @param parameterList - string representation of a parameter list
	 * @param classPath     - classpath where the parameter types may reside
	 * @return - {@code Class<?>[]}
	 */
	public static Class<?>[] extractParameterTypes(String parameterList, String classPath) {
		// Extract inner parameters (remove parenthesis)
		String innerParameters = parameterList.substring(parameterList.indexOf('(') + 1, parameterList.indexOf(')'));

		// Split and stream on each individual parameter type (string representation)
		List<Class<?>> typeObjects = Pattern.compile(",").splitAsStream(innerParameters)
				.map(parameterType -> Types.getTypeSignature(parameterType, true)).map(typeSignature -> {
					// Try to reflect the type on default classpath (project buildpath)
					try {
						return Class.forName(typeSignature);
					} catch (ClassNotFoundException e) {
						// Try to reflect the type on user supplied classpath
						URLClassLoader cl;
						try {
							cl = new URLClassLoader(new URL[] { new File(classPath).toURI().toURL() });
						} catch (MalformedURLException e1) {
							cl = null;
						}
						try {
							return Class.forName(typeSignature, true, cl);
						} catch (ClassNotFoundException e2) {
							// It's probably a primitive
							switch (typeSignature) {
							case "B":
								return byte.class;
							case "C":
								return char.class;
							case "S":
								return short.class;
							case "I":
								return int.class;
							case "F":
								return float.class;
							case "J":
								return long.class;
							case "D":
								return double.class;
							case "Z":
								return boolean.class;
							case "V":
								return void.class;
							default:
								return null;
							}
						}
					}
				}).collect(Collectors.toList());
		return typeObjects.toArray(new Class<?>[typeObjects.size()]);
	}

	/**
	 * Extracts class type for a given class name on a potential classpath
	 * 
	 * @param className - name of class to extract
	 * @param classPath - potential classpath of class
	 * @return {@code Class<?>} - Class type of class
	 */
	public static Class<?> extractClass(String className, String classPath) {
		// Try to reflect on default classpath
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			// Try to reflect on supplied classpath
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

	/**
	 * Extracts method object for a given class, method name, and parameter types.
	 * 
	 * @param clazz          - class of method to extract
	 * @param methodName     - name of method to extract
	 * @param parameterTypes - parameter types of method to extract
	 * @return {@code Method} - Method object representing extracted method
	 */
	public static Method extractMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Extracts Field object for a given class and field name
	 * 
	 * @param clazz     - class containing field to extract
	 * @param fieldName - name of field to extract
	 * @return {@code Field} - Field object of extracted field.
	 */
	public static Field extractField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

	/**
	 * Converts a method signature (class name and signature) to it's Java Native
	 * Interface representation
	 * 
	 * @param className - class containing method
	 * @param signature - signature of method
	 * @return {@code String} - string containing the method signature's JNI
	 *         representation
	 */
	public static String methodSignatureToJNI(String className, String signature) {
		StringBuilder s = new StringBuilder();
		s.append(className.replace('.', '_'));
		s.append('_');

		for (char c : signature.toCharArray()) {
			switch (c) {
			case '/':
				s.append('_');
				break;
			case '_':
				s.append("_1");
				break;
			case ';':
				s.append("_2");
				break;
			case '[':
				s.append("_3");
				break;
			case ')':
				s.append("__");
				break;
			default:
				s.append(c);
			}
		}
		return s.toString();
	}
}
