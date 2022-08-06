/*
 * Copyright (C)  2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package jpf.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import gov.nasa.jpf.vm.Types;

/**
 * A representation of a jpf-label label within the jpf-logic context.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public abstract class Label {

	public abstract String getLabelClass();
	
	public abstract Properties getProperties(); 
	
	public abstract String getMangledName();
	
	/**
	 * It returns the Class object corresponding to the given string representation.
	 * 
	 * @param type a string representation of a type
	 * @return the Class object corresponding to the given string representation
	 * @throws ClassNotFoundException if the corresponding Class object cannot be found
	 */
	private static Class<?> getClass(String type) throws ClassNotFoundException {
		int arrayDimension = 0;
		while (type.endsWith("[]")) { // arrays
			type = type.substring(0, type.length() - "[]".length());
			arrayDimension++;
		}
		if (arrayDimension > 0) {
			String name;
			if (type.equals("byte")) {
				name = "B";
			} else if (type.equals("char")) {
				name = "C";
			} else if (type.equals("short")) {
				name = "S";
			} else if (type.equals("int")) {
				name = "I";
			} else if (type.equals("float")) {
				name = "F";
			} else if (type.equals("long")) {
				name = "J";
			} else if (type.equals("double")) {
				name = "D";
			} else if (type.equals("boolean")) {
				name = "Z";
			} else {
				name = "L" + type + ';';
			}
			while (arrayDimension-- > 0) {
				name = "[" + name;
			}
			return Class.forName(name);
		} else {
			if (type.equals("byte")) {
				return byte.class;
			} else if (type.equals("char")) {
				return char.class;
			} else if (type.equals("short")) {
				return short.class;
			} else if (type.equals("int")) {
				return int.class;
			} else if (type.equals("float")) {
				return float.class;
			} else if (type.equals("long")) {
				return long.class;
			} else if (type.equals("double")) {
				return double.class;
			} else if (type.equals("boolean")) {
				return boolean.class;
			} else {
				return Class.forName(type);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param name
	 * @param parameters
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static String getMangledName(String name, String parameters) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		int lastDot = name.lastIndexOf('.');
		String className = name.substring(0, lastDot);
		Class<?> clazz = Class.forName(className);

		String methodName = name.substring(lastDot + 1);

		Class<?>[] typesArray;
		if (parameters.equals("()")) {
			typesArray = new Class<?>[0];
		} else {
			parameters = parameters.substring(1, parameters.length() - 1); // delete ( and )
			String[] types = parameters.split(",");
			List<Class<?>> typesList = new ArrayList<Class<?>>();
			for (String type : types) {
				typesList.add(getClass(type));
			}
			typesArray = new Class<?>[typesList.size()];
			typesList.toArray(typesArray);
		}

		Method method = clazz.getDeclaredMethod(methodName, typesArray);
		
		return className.replace('.', '_') + "_" + Types.getJNIMangledMethodName(method);
	}
}
