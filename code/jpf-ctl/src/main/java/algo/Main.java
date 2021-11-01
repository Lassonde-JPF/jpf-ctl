package algo;

import java.io.File;
import java.util.List;

import config.Result;
import config.StructuredCTLConfig;
import logging.Logger;

import org.apache.commons.cli.*;

public class Main {

	private static final String JPF_CTL = "jpf-ctl";

	// main entry loop for command line version
	public static void main(String[] args) {

		// Parse command line arguments.
		Options options = new Options();

		// Required Options
		options.addRequiredOption("c", "configPath", true, "config gile path");
		options.addRequiredOption("t", "targetPath", true, "target file path");

		// Optional Options
		options.addOption(new Option("e", "enumerateRandom", false, "consider randomness"));
		options.addOption(new Option("args", "targetArgs", true, "cmd line arguments for target"));

		// Parse Options
		CommandLine cmd = null;
		File configFile = null, targetFile = null;
		String targetArgs = null, enumerateRandom = null;
		try {
			cmd = new DefaultParser().parse(options, args);

			String configPath = cmd.getOptionValue("configPath");
			configFile = new File(configPath);
			if (!configFile.exists()) {
				throw new ParseException("could not find file specified by: " + configPath);
			}

			String targetPath = cmd.getOptionValue("targetPath");
			targetFile = new File(targetPath);
			if (!targetFile.exists()) {
				throw new ParseException("could not find file specified by: " + targetPath);
			}

			targetArgs = cmd.getOptionValue("targetArgs");
			enumerateRandom = cmd.hasOption("e") ? "true" : "false";
		} catch (ParseException e) {
			new HelpFormatter().printHelp(JPF_CTL, options);
			System.exit(1);
		}

		// Initialize logging service/obj
		Logger logger = new Logger(Main.class.getName(), "Main");
		logger.setOutputFile(JPF_CTL + "-" + System.currentTimeMillis());
		logger.info("Model Checking Started");

		// Load config
		StructuredCTLConfig config = null;
		try {
			config = new StructuredCTLConfig(configFile, targetFile, targetArgs, enumerateRandom);
		} catch (Exception e) {
			logger.severe("Error building CTL specification: " + e);
			System.exit(1);
		}

		// Build checker and results object(s)
		Checker checker = new Checker(config);

		// Perform model check
		try {
			List<Result> results = checker.validate();
			// Relay results back to user
			for (Result r : results) {
				System.out.println(r.getMessage());
			}
		} catch (Exception e) {
			logger.severe("Error performing validation " + e);
			e.printStackTrace();
			System.exit(1);
		}

	}

}
