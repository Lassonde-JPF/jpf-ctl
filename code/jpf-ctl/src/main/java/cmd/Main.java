package cmd;

import java.io.File;
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

	// main entry loop for command line version test
	public static void main(String[] args) {

		// Parse command line arguments
		Options options = new Options();

		// Required Options
		options.addRequiredOption("c", "configPath", true, "config gile path");
		options.addRequiredOption("t", "targetPath", true, "target file path");

		// Optional Options
		options.addOption(new Option("e", "enumerateRandom", false, "consider randomness"));
		options.addOption(new Option("l", "logging", false, "enable logging"));
		options.addOption(new Option("args", "targetArgs", true, "cmd line arguments for target"));

		// Parse Options
		CommandLine cmd = null;
		File configFile = null, targetFile = null;
		String targetArgs = null, enumerateRandom = null;
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
			String targetPath = cmd.getOptionValue("targetPath");
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
			targetFile = new File(targetPath);
			if (!targetFile.exists()) {
				throw new ParseException("could not find file specified by: " + targetPath);
			}

			// build remaining arguments
			targetArgs = cmd.getOptionValue("targetArgs");
			enumerateRandom = cmd.hasOption("e") ? "true" : "false";
			Logger.setEnabled(cmd.hasOption("l"));
		} catch (ParseException e) {
			new HelpFormatter().printHelp(JPF_CTL, options);
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
			config = new StructuredCTLConfig(configFile, targetFile, targetArgs, enumerateRandom);
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
				logger.info(r.toString());
			}
		} catch (Exception e) {
			logger.severe("Error performing validation " + e);
			e.printStackTrace();
			System.exit(1);
		}

	}

}
