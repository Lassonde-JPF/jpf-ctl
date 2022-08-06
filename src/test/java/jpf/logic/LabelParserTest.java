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
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package jpf.logic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jpf.logic.LabelLexer;
import jpf.logic.LabelParser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the jpf.logic.LabelParser class.
 * 
 * @author Franck van Breugel
 */
class LabelParserTest {

	private static final List<Class<?>> classes = getClasses();
	private static final List<Class<?>> exceptionClasses = getExceptionClasses();
	private static final List<Method> methods = getMethods();
	private static final List<Method> staticSynchronizedMethods = getStaticSynchronizedMethods();
	private static final List<Method> voidMethods = getVoidMethods();
	private static final List<Field> staticBooleanFields = getStaticBooleanFields();

	/**
	 * Returns the list of classes of rt.jar.
	 * 
	 * @return the list of classes of rt.jar
	 */
	private static List<Class<?>> getClasses() {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String bootPath = System.getProperty("sun.boot.class.path");
		String path = null;
		for (String part : bootPath.split(";")) {
			if (part.endsWith("rt.jar")) {
				path = part;
			}
		}
		if (path != null) {
			File file = new File(path.replaceAll("\\\\", "/"));
			Class<?> clazz = null;
			String entry = null;
			try {
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					entry = entries.nextElement().toString();
					entry = entry.replaceAll("/", ".");
					if (entry.endsWith(".class")) {
						entry = entry.substring(0, entry.length() - ".class".length());
						clazz = Class.forName(entry);
						classes.add(clazz);
					}
				}
				jarFile.close();
			} catch (IOException e) {
				System.err.println("Error: reading " + file);
			} catch (ClassNotFoundException | ExceptionInInitializerError e) {
				System.err.println("Warning: cannot access " + clazz);
			}
		} else {
			System.err.println("Error: File rt.jar cannot be found");
		}
		return classes;
	}

	/**
	 * Tests whether the given class is an exception class.
	 * 
	 * @param clazz a class
	 * @return true if the given class is an exception class,
	 * false otherwise
	 */
	private static boolean isExceptionClass(Class<?> clazz) {
		boolean isExceptionClass = false;
		while (clazz != null && !isExceptionClass) {
			if (clazz.equals(Exception.class)) {
				isExceptionClass = true;
			} else {
				clazz = clazz.getSuperclass();
			}
		}
		return isExceptionClass;
	}

	/**
	 * Returns the list of exception classes of rt.jar.
	 * 
	 * @return the list of exception classes of rt.jar
	 */
	private static List<Class<?>> getExceptionClasses() {
		List<Class<?>> exceptionClasses = new ArrayList<Class<?>>();
		for (Class<?> clazz : classes) {
			if (isExceptionClass(clazz)) {
				exceptionClasses.add(clazz);
			}
		}
		return exceptionClasses;
	}

	/**
	 * Returns the list of methods of rt.jar.
	 * 
	 * @return the list of methods of rt.jar
	 */
	private static List<Method> getMethods() {
		List<Method> methods = new ArrayList<Method>();
		for (Class<?> clazz : classes) {
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		}
		return methods;
	}

	/**
	 * Returns the list of static synchronized methods of rt.jar.
	 * 
	 * @return the list of static synchronized methods of rt.jar
	 */
	private static List<Method> getStaticSynchronizedMethods() {
		List<Method> staticSynchronizedMethods = new ArrayList<Method>();
		for (Method method : methods) {
			int modifiers = method.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isSynchronized(modifiers)) {
				staticSynchronizedMethods.add(method);
			}
		}
		return staticSynchronizedMethods;
	}

	/**
	 * Returns the list of void methods of rt.jar.
	 * 
	 * @return the list of void methods of rt.jar
	 */
	private static List<Method> getVoidMethods() {
		List<Method> voidMethods = new ArrayList<Method>();
		for (Method method : methods) {
			if (method.getReturnType().equals(Void.TYPE)) {
				voidMethods.add(method);
			}
		}
		return voidMethods;
	}

	/**
	 * Returns the list of static boolean fields of rt.jar.
	 * 
	 * @return the list of static boolean fields of rt.jar
	 */
	private static List<Field> getStaticBooleanFields() {
		List<Field> staticBooleanFields = new ArrayList<Field>();
		for (Class<?> clazz : classes) {
			for (Field field : clazz.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) && field.getType().equals(Boolean.TYPE)) {
					staticBooleanFields.add(field);
				}
			}
		}
		return staticBooleanFields;
	}

	/**
	 * Returns the signature of the given method.
	 * The signature consists of the name and the parameter types of the method. 
	 * 
	 * @param method a method
	 * @return the signature of the method 
	 */
	private static String methodToString(Method method) {
		final String[] modifiers = { "public ", 
				"protected ",
				"private ",
				"abstract ",
				"default ",
				"static ",
				"final ",
				"synchronized ",
				"native ",
				"strictfp " 
		};

		String representation = method.toString();

		// remove throws ...
		if (representation.contains(" throws")) {  
			int index = representation.indexOf(" throws");
			representation = representation.substring(0, index);
		}

		// remove modifiers
		for (String modifier : modifiers) {
			if (representation.contains(modifier)) { 
				int index = representation.indexOf(modifier);
				representation = representation.substring(index + modifier.length());
			}
		}

		// remove return type
		int index = representation.indexOf(" "); 
		representation = representation.substring(index + 1);

		return representation;
	}

	/**
	 * Returns fully qualified name of the given field.
	 * 
	 * @param field a field
	 * @return the fully qualified name of the field
	 */
	private static String fieldToString(Field field) {
		final String[] modifiers = { "public ", 
				"protected ",
				"private ",
				"static ", 
				"final ", 
				"transient ", 
				"volatile "
		};

		String representation = field.toString();

		// remove modifiers
		for (String modifier : modifiers) {
			if (representation.contains(modifier)) { 
				int index = representation.indexOf(modifier);
				representation = representation.substring(index + modifier.length());
			}
		}

		// remove type
		int index = representation.indexOf(" "); 
		representation = representation.substring(index + 1);

		return representation;
	}

	/**
	 * Randomness.
	 */
	private static final Random random = new Random();

	/**
	 * Returns a random identifier.
	 * 
	 * @return a random identifier
	 */
	private static String randomIdentifier() {
		final int MAX_LENGTH = 25;
		final String[] keywords = { "abstract", "assert", "boolean",
				"break", "byte", "case", "catch", "char", "class", "const",
				"continue", "default", "do", "double", "else", "extends", "false",
				"final", "finally", "float", "for", "goto", "if", "implements",
				"import", "instanceof", "int", "interface", "long", "native",
				"new", "null", "package", "private", "protected", "public",
				"return", "short", "static", "strictfp", "super", "switch",
				"synchronized", "this", "throw", "throws", "transient", "true",
				"try", "void", "volatile", "while" };

		String identifier = "";
		do {
			int length = 1 + random.nextInt(MAX_LENGTH);
			for (int i = 0; i < length; i++) {
				if (random.nextBoolean()) {
					identifier += (char) ('a' + random.nextInt('z' - 'a'));
				} else {
					identifier += (char) ('A' + random.nextInt('Z' - 'A'));
				}
			}
		} while (Arrays.asList(keywords).contains(identifier));

		return identifier;
	}

	/**
	 * Returns the parse tree of the given input string.
	 * 
	 * @param input a string
	 * @return the parse tree of the given input string
	 */
	private static ParseTree parse(String input) {	
		LabelLexer lexer = new LabelLexer(CharStreams.fromString(input));
		lexer.removeErrorListeners();
		LabelParser parser = new LabelParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners();
		ParseTree tree = parser.label();
		return tree;
	}

	/**
	 * Tests the Initial label.
	 */
	@Test
	void testInitial() {
		ParseTree tree = parse("Initial");
		Assertions.assertEquals("Initial", tree.getText());
	}

	/**
	 * Tests the End label.
	 */
	@Test
	void testEnd() {
		ParseTree tree = parse("End");
		Assertions.assertEquals("End", tree.getText());
	}

	/**
	 * Tests the BooleanStaticField label.
	 */
	@Test
	void testBooleanStaticField() {
		boolean[] booleans = { true, false };
		for (Field field : staticBooleanFields) {
			for (boolean value : booleans) {
				String input = "BooleanStaticField " + fieldToString(field) + " " + value;
				ParseTree tree = parse(input);
				Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
			}
		}
	}

	/**
	 * Tests the BooleanLocalVariable label.
	 */
	@Test
	void testBooleanLocalVariable() {
		boolean[] booleans = { true, false };
		for (Method method : voidMethods) {
			for (boolean value : booleans) {
				String input = "BooleanLocalVariable " + methodToString(method) + " : " + randomIdentifier() + " " + value;
				ParseTree tree = parse(input);
				Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
			}
		}
	}

	/**
	 * Tests the InvokedMethod label.
	 */
	@Test
	void testInvokedMethod() {
		for (Method method : methods) {
			String input = "InvokedMethod " + methodToString(method);
			ParseTree tree = parse(input);
			Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
		}
	}

	/**
	 * Tests the ReturnedVoidMethod label.
	 */
	@Test
	void testReturnedVoidMethod() {
		for (Method method : voidMethods) {
			String input = "ReturnedVoidMethod " + methodToString(method);
			ParseTree tree = parse(input);
			Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
		}
	}

	/**
	 * Tests the ReturnedBooleanMethod label.
	 */
	@Test
	void testReturnedBooleanMethod() {
		boolean[] booleans = { true, false };
		for (Method method : voidMethods) {
			for (boolean value : booleans) {
				String input = "ReturnedBooleanMethod " + methodToString(method) + " " + value;
				ParseTree tree = parse(input);
				Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
			}
		}
	}

	/**
	 * Tests the ThrownException label.
	 */
	@Test
	void testThrownException() {
		for (Class<?> clazz : exceptionClasses) {
			String input = "ThrownException " + clazz.getName();
			ParseTree tree = parse(input);
			Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
		}
	}

	/**
	 * Tests the SynchronizedStaticMethod label.
	 */
	@Test
	void testSynchronizedStaticMethod() {
		for (Method method : staticSynchronizedMethods) {
			String input = "SynchronizedStaticMethod " + methodToString(method);
			ParseTree tree = parse(input);
			Assertions.assertEquals(input.replaceAll(" ", ""), tree.getText());
		}
	}
}
