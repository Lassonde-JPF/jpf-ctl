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
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field extractField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

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
