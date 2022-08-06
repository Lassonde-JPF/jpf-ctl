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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 
 * 
 * @author Franck van Breugel
 */
public class Main {

	/**
	 * Help message.
	 */
	private static final String HELP = "Usage:\n"
			+ "  java jpf.logic.Main -help                 prints this message\n"
			+ "  java jpf.logic.Main sample.jpf            runs jpf-logic with configuration file sample.jpf\n"
			+ "  java jpf.logic.Main -verbose sample.jpf   runs jpf-logic with configuration file sample.jpf in verbose mode";

	/**
	 * 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("jpf-logic");

		// handle command line arguments
		String fileName = null;
		if (args.length == 0 || args[0].equals("-help")) {
			System.out.println(HELP);
			System.exit(0);
		} else if (args[0].endsWith(".jpf")) {
			logger.setLevel(Level.SEVERE);
			fileName = args[0];
		} else if (args.length > 1 && args[0].equals("-verbose") && args[1].endsWith(".jpf")) {
			logger.setLevel(Level.INFO);
			fileName = args[1];
		} else {
			System.out.println(HELP);
			System.exit(0);
		}

		File file = new File(fileName);
		if (file.exists() && !file.isDirectory()) { 
			logger.info("Configuration file " + file + " is found");
		} else {
			logger.severe("Configuration file " + file + " cannot be found");
			System.exit(0);
		}

		Properties properties = new Properties();
		try {
			FileInputStream stream = new FileInputStream(file);
			logger.info("Configuration file " + file + " is read");
			properties.load(stream);
			logger.info("Properties of configuration file " + file + " are loaded");
			stream.close();
		} catch (FileNotFoundException e) {
			logger.severe("Configuration file " + file + " cannot be read");
			System.exit(0);
		} catch (IOException e) {
			logger.severe("Configuration file " + file + " cannot be closed");
			System.exit(0);
		}

		String formulaFileName = properties.getProperty("jpf.logic.formula");
		if (formulaFileName == null) {
			logger.severe("Property jpf.logic.formula cannot be found");
		} else {
			logger.info("Property jpf.logic.formula is found");
		}

		File formulaFile = new File(formulaFileName);
		if (formulaFile.exists() && !formulaFile.isDirectory()) { 
			logger.info("Formula file " + formulaFile + " is found");
		} else {
			logger.severe("Formula file " + formulaFile + " cannot be found");
			System.exit(0);
		}

		String parserName = properties.getProperty("jpf.logic.parser");
		if (parserName == null) {
			logger.severe("Property jpf.logic.parser cannot be found");
			System.exit(0);
		} else {
			logger.info("Property jpf.logic.parser is found");
		}

		FormulaParser formulaParser = null;
		try {
			Class<?> clazz = Class.forName(parserName);
			logger.info("Class " + parserName + " is found");
			Constructor<?> constructor = clazz.getConstructor();
			logger.info("Constructor of class " + parserName + " is found");
			formulaParser = (FormulaParser) constructor.newInstance(new Object[] { });
			logger.info("Instance of class " + parserName + " is constructed");
		} catch (ClassNotFoundException e) {
			logger.severe("Class " + parserName + " cannot be found");
			System.exit(0);
		} catch (NoSuchMethodException e) {
			logger.severe("Constructor of class " + parserName + " cannot be found");
			System.exit(0);
		} catch (SecurityException | IllegalAccessException e) {
			logger.severe("Constructor of class " + parserName + " cannot be accessed");
			System.exit(0);
		} catch (InstantiationException e) {
			logger.severe("Class " + parserName + " is abstract");
			System.exit(0);
		} catch (IllegalArgumentException e) {
			logger.severe("Constructor of class " + parserName + " should not have parameters");
			System.exit(0);
		} catch (InvocationTargetException e) {
			logger.severe("Constructor of class " + parserName + " throws an exception");
			System.exit(0);
		}

		String target = properties.getProperty("target");
		if (target == null) {
			logger.severe("Property target cannot be found");
			System.exit(0);
		} else {
			logger.info("Property target is found");
		}

		String classpath = properties.getProperty("classpath");
		if (classpath == null) {
			logger.severe("Property classpath cannot be found");
			System.exit(0);
		} else {
			logger.info("Property classpath is found");
		}

		// set listeners
		String listener = properties.getProperty("listener");
		if (listener == null) {
			listener = "jpf.logic.PartialTransitionSystemListener;label.StateLabelText";
		} else {
			listener += ";jpf.logic.PartialTransitionSystemListener;label.StateLabelText";
		}
		properties.setProperty("listener", listener);
		logger.info("Set property listener to " + listener);

		// add listeners to native_classpath
		String nativeClasspath = properties.getProperty("native_classpath");
		if (nativeClasspath == null) {
			nativeClasspath = "${jpf-logic}/build/libs/jpf-logic.jar;${jpf-label}/build/jpf-label.jar";
		} else {
			nativeClasspath += ";${jpf-logic}/build/libs/jpf-logic.jar;${jpf-label}/build/jpf-label.jar";
		}
		properties.setProperty("native_classpath", nativeClasspath);
		logger.info("Set property native_classpath to " + nativeClasspath);

		// use jpf-label
		properties.setProperty("@using", "jpf-label");
		logger.info("Use jpf-label");

		// parse file with aliases and formula 
		Formula formula = null;
		Map<String, String> namesToAliases = new HashMap<String, String>();
		try {
			Scanner input = new Scanner(formulaFile);
			int numberOfFormulas = 0;
			while (input.hasNextLine()) {
				String line = input.nextLine().trim();
				if (line.isEmpty() || line.startsWith("#")) {
					// skip
				} else if (line.contains(":")) { // alias
					String[] part = line.split(":");
					if (part.length != 2) {
						logger.severe("Alias definition " + line + " is invalid");
						System.exit(0);
					} 
					String alias = part[0].trim();
					String atomicProposition = part[1].trim();
					logger.info(alias + " is an alias for " + atomicProposition);

					Label label = MyLabelParser.parse(atomicProposition);

					String labelClass = properties.getProperty("label.class");
					if (labelClass == null) {
						labelClass = label.getLabelClass();
					} else {
						labelClass += ";" + label.getLabelClass();
					}
					properties.setProperty("label.class", labelClass);
					logger.info("Add " + label.getLabelClass() + " to label.class");

					for (String key : label.getProperties().stringPropertyNames()) {
						String value = label.getProperties().getProperty(key);
						String oldValue = properties.getProperty(key);
						if (oldValue != null) {
							value = oldValue + ";" + value;
						}
						properties.setProperty(key, value);
						logger.info("Add " + value + " to " + key);
					}

					String mangledName = label.getMangledName();
					namesToAliases.put(mangledName, alias);
					logger.info("Map " + mangledName + " to " + alias);
				} else { // formula
					if (numberOfFormulas == 1) {
						logger.severe("Line " + line + " is invalid (maybe more than one formula?)");
						System.exit(0);
					}
					formula = formulaParser.parse(line);
					String lineWithoutParentheses = line.replaceAll("\\(", "").replaceAll("\\)", "");
					String formulaWithoutParentheses = formula.toString().replaceAll("\\(", "").replaceAll("\\)", "");
					if (!lineWithoutParentheses.equals(formulaWithoutParentheses)) {
						logger.severe("Formula " + line + " is invalid");
						System.exit(0);
					}
					logger.info(formula + " is a formula");
					numberOfFormulas++;
				}
			}
			input.close();
			if (numberOfFormulas == 0) {
				logger.severe("No formula is found");
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			logger.severe("Formula file " + formulaFile + " cannot be found");
			System.exit(0);
		}

		// run JPF
		Config configuration = new Config(new String[] {});
		configuration.putAll(properties);
		logger.info(String.format("Create JPF configuration\n  target = %s\n  classpath = %s\n  native_classpath = %s\n  listener = %s\n  label.class = %s", 
				configuration.getProperty("target"),
				configuration.getProperty("classpath"),
				configuration.getProperty("native_classpath"),
				configuration.getProperty("listener"),
				configuration.getProperty("label.class")));
		JPF jpf = new JPF(configuration);
		logger.info("Run JPF");
		
		PrintStream stdout = System.out;	
		try {
			if (logger.getLevel().equals(Level.SEVERE)) {
				System.setOut(new PrintStream(target + ".log"));
			}
		} catch (FileNotFoundException e1) {
			logger.severe("Cannot write JPF's output to " + target + ".log");
			System.exit(0);
		}		
		jpf.run();
		System.setOut(stdout);
		
		// replace mangled names by aliases in .lab file
		String inputFileName = target + ".lab";
		String outputFileName = "temp.lab";
		
		File input = new File(inputFileName);
		File output = new File(outputFileName);
		
		try {
			FileReader fileReader = new FileReader(input);
			BufferedReader reader = new BufferedReader(fileReader);

			try {
				FileWriter fileWriter = new FileWriter(output);
				BufferedWriter writer = new BufferedWriter(fileWriter);
				String inputLine = reader.readLine();
				String outputLine = "";
				String[] mappings = inputLine.split(" ");
				for (String mapping : mappings) {
					String[] part = mapping.split("=");
					String alias = namesToAliases.get(part[1]);
					if (alias != null) {
						outputLine += part[0]; // index
						outputLine += "=";
						outputLine += alias;
						outputLine += " ";
						logger.info("Replace " + part[1] + " with " + alias);
					}
				}
				writer.write(outputLine, 0, outputLine.length());
				writer.newLine();

				final int BUFFER_SIZE = 512;
				char[] buffer = new char[BUFFER_SIZE];
				int length;
				do {
					length = reader.read(buffer, 0, BUFFER_SIZE);
					if (length != -1) {
						writer.write(buffer, 0, length);
					}
				} while (length != -1);

				reader.close();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				logger.severe("File " + outputFileName + " cannot be written or file " + inputFileName + " cannot be read");
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			logger.severe("File " + inputFileName + " cannot be found");
			System.exit(0);
		}
		
		if (input.delete()) {
			logger.info("File " + inputFileName + " is deleted");
		} else {
			logger.severe("File " + inputFileName + " cannot be deleted");
		}
		
		if (output.renameTo(input)) {
			logger.info("File " + outputFileName + " is renamed to " + inputFileName);
		} else {
			logger.severe("File " + outputFileName + " cannot be renamed to " + inputFileName);
		}
		
		String modelCheckerName = properties.getProperty("jpf.logic.model-checker");
		if (modelCheckerName == null) {
			logger.severe("Property jpf.logic.model-checker cannot be found");
			System.exit(0);
		} else {
			logger.info("Property jpf.logic.model-checker is found");
		}

		ModelChecker modelChecker = null;
		try {
			Class<?> clazz = Class.forName(modelCheckerName);
			logger.info("Class " + modelCheckerName + " is found");
			Constructor<?> constructor = clazz.getConstructor();
			logger.info("Constructor of class " + modelCheckerName + " is found");
			modelChecker = (ModelChecker) constructor.newInstance(new Object[] { });
			logger.info("Instance of class " + modelCheckerName + " is constructed");
		} catch (ClassNotFoundException e) {
			logger.severe("Class " + modelCheckerName + " cannot be found");
			System.exit(0);
		} catch (NoSuchMethodException e) {
			logger.severe("Constructor of class " + modelCheckerName + " cannot be found");
			System.exit(0);
		} catch (SecurityException | IllegalAccessException e) {
			logger.severe("Constructor of class " + modelCheckerName + " cannot be accessed");
			System.exit(0);
		} catch (InstantiationException e) {
			logger.severe("Class " + modelCheckerName + " is abstract");
			System.exit(0);
		} catch (IllegalArgumentException e) {
			logger.severe("Constructor of class " + modelCheckerName + " should not have parameters");
			System.exit(0);
		} catch (InvocationTargetException e) {
			logger.severe("Constructor of class " + modelCheckerName + " throws an exception");
			System.exit(0);
		}
		
		PartialTransitionSystem system = null;
		try {
			system = new PartialTransitionSystem(target);
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("File " + target + " .tra/.lab cannot be read");
			System.exit(0);
		}
		logger.info("Create partial transition system");
		
		modelChecker.setPartialTransitionSystem(system);
		Result result = modelChecker.check(formula);
		logger.info("Run model checker");
		
		if (result.getLower().get(0)) {
			System.out.println("The formula holds");
		} else if (!result.getUpper().get(0)) {
			System.out.println("The formula does not hold");
		} else {
			System.out.println("The formula may hold");
		}
		
		//System.out.println("Lower: " + result.getLower());
		//System.out.println("Upper: " + result.getUpper());
	}
}
