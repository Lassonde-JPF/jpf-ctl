package cmd;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import config.Result;
import config.StructuredCTLConfig;
import logging.Logger;

import org.apache.commons.cli.*;

import algo.Checker;

public class Main {

	// Static Variables
	private static final String JPF_CTL = "jpf-ctl";
	private static final String CTL_EXTENSION = "ctl";
	private static final String CLASS_EXTENSION = "class";
	private static final String DOT = ".";

	// Help Printer
	private static final String HEADER
			= "\n"
			+ "    o8o             .o88o.                       .   oooo  \r\n"
			+ "    `\"'             888 `\"                     .o8   `888  \r\n"
			+ "   oooo oo.ooooo.  o888oo           .ooooo.  .o888oo  888  \r\n"
			+ "   `888  888' `88b  888            d88' `\"Y8   888    888  \r\n"
			+ "    888  888   888  888    8888888 888         888    888  \r\n"
			+ "    888  888   888  888            888   .o8   888 .  888  \r\n"
			+ "    888  888bod8P' o888o           `Y8bod8P'   \"888\" o888o \r\n"
			+ "    888  888                                               \r\n"
			+ ".o. 88P o888o                                              \r\n"
			+ "`Y888P                                                     \n\n";
	private static final String FOOTER 
			= "\nPlease report issues at https://github.com/Lassonde-JPF/jpf-ctl/issues";

	// main entry loop for command line version test
	public static void main(String[] args) {

		// Parse command line arguments
		Options options = new Options();

		// Required Options
		options.addRequiredOption("ctl", "configPath", true, "path to ctl config file");
		options.addRequiredOption("tn", "targetName", true, "qualified name of target class");
		options.addRequiredOption("tp", "classpath", true, "classpath of target");

		// Optional Options
		options.addOption(new Option("e", "enumerateRandom", false, "consider randomness"));
		options.addOption(new Option("v", "verboseLogs", false, "enable logging to print to console"));
		options.addOption(new Option("ta", "targetArgs", true, "cmd line arguments for target"));

		// Parse Options
		CommandLine cmd = null;
		File configFile = null;
		String targetArgs = null, enumerateRandom = null, targetName = null, targetClasspath = null;
		try {
			cmd = new DefaultParser().parse(options, args);

			// Check that config file exists and is correct
			String configPath = cmd.getOptionValue("configPath");
			try {
				String extension = configPath.substring(configPath.lastIndexOf(DOT) + 1);
				if (!extension.equals(CTL_EXTENSION)) {
					throw new ParseException("file has the wrong extension, expected '" + CTL_EXTENSION + "' for "
							+ configPath + " but was " + extension);
				}
			} catch (IndexOutOfBoundsException e) {
				throw new ParseException(
						"file does not contain an extension, expected '" + CTL_EXTENSION + "' for " + configPath);
			}
			configFile = new File(configPath);
			if (!configFile.exists()) {
				throw new ParseException("could not find file specified by: " + configPath);
			}

			// Check that target file exists and is correct
			String targetPath = cmd.getOptionValue("classpath") + cmd.getOptionValue("targetName").replaceAll("\\.", "/") + ".class";
			try {
				String extension = targetPath.substring(targetPath.lastIndexOf(DOT) + 1);
				if (!extension.equals(CLASS_EXTENSION)) {
					throw new ParseException("file has the wrong extension, expected '" + CLASS_EXTENSION + "' for "
							+ targetPath + " but was " + extension);
				}
			} catch (IndexOutOfBoundsException e) {
				throw new ParseException(
						"file does not contain an extension, expected '" + CLASS_EXTENSION + "' for " + configPath);
			}
			File targetFile = new File(targetPath);
			if (!targetFile.exists()) {
				throw new ParseException("could not find file specified by: " + targetPath);
			}
			targetName = cmd.getOptionValue("targetName");
			targetClasspath = cmd.getOptionValue("classpath");

			// build remaining arguments
			targetArgs = cmd.getOptionValue("targetArgs");
			enumerateRandom = cmd.hasOption("e") ? "true" : "false";
			Logger.setEnabled(cmd.hasOption("v"));
		} catch (ParseException e) {
			new HelpFormatter().printHelp(JPF_CTL, HEADER, options, FOOTER, true);
			System.out.println(e.getMessage());
			System.exit(1);
		}

		// Initialize logging service/obj
		Logger logger = new Logger(Main.class.getName(), "Main");
		try {
			logger.setOutputFile(JPF_CTL + "-" + System.currentTimeMillis());
		} catch (Exception e) {
			logger.warning("Error adding file handler to logger, logs will not be saved for this execution" + e);
		}

		// Load config
		StructuredCTLConfig config = null;
		try {
			config = new StructuredCTLConfig(configFile, targetName, targetClasspath, targetArgs, enumerateRandom);
		} catch (Exception e) {
			logger.severe("Error building CTL specification: " + e);
			e.printStackTrace();
			System.exit(1);
		}

		logger.info("config\n" + config.toString());

		// Build checker and results object(s)
		Checker checker = new Checker(config);

		// Perform model check
		try {
			List<Result> results = checker.validate();
			// Relay results back to user
			for (Result r : results) {
				String msg = "Model Checking finished for " + r.getTarget().getName() + " and " + r.getFormula() + "\n";
				if (r.isValid()) {
					msg += "It has been determined that the formula holds in the initial state as is considered valid for this system.";
					logger.info(msg);
				} else {
					Path counterExamplePath = Paths.get("counterExamples/" + r.getTarget().getName() + ".ce");
					Files.createDirectories(counterExamplePath.getParent());
					Files.write(counterExamplePath, r.getCounterExample().getBytes());
					msg += "It has been determined that the formula does not hold in the initial state and is considered invalid for this system.\nA counter example can be found at "
							+ counterExamplePath.toAbsolutePath().normalize();
					logger.info(msg);
				}
			}
		} catch (Exception e) {
			logger.severe("Error performing validation " + e);
			e.printStackTrace();
			System.exit(1);
		}

	}

}
