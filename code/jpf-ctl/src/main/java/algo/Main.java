package algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import config.Result;
import config.StructuredCTLConfig;
import ctl.Formula;
import logging.Logger;

import org.apache.commons.cli.*;

public class Main {
	
	private static final String JPF_CTL = "jpf-ctl";
	
	// main entry loop for command line version
	public static void main(String[] args) {
		
		// Parse command line arguments.
		Options options = buildOptions(args);
		CommandLine cmd = null;
		try {
			cmd = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			new HelpFormatter().printHelp(JPF_CTL, options);
			System.exit(1);
		}

		// Load args as objects
		String configPath = cmd.getOptionValue("configPath");
		String targetPath = cmd.getOptionValue("targetPath");
		String targetArgs = cmd.getOptionValue("targetArgs");
		String enumerateRandom = cmd.hasOption("e") ? "True" : "False";
		boolean pack = cmd.hasOption("p");
		
		// Initialize logging service/obj
		Logger logger = new Logger(Main.class.getName(), "Main");
		logger.setOutputFile(JPF_CTL + "-" + System.currentTimeMillis());
		logger.info("Model Checking Started");
		
		// Load config
		StructuredCTLConfig config = null;
		try {
			config = new StructuredCTLConfig(configPath);
		} catch (Exception e) {
			logger.severe("Error loading config file " + configPath + "\n" + e);
		}
		
		// Build checker and results object(s)
		Checker checker = new Checker(config);
		List<Result> results = new ArrayList<Result>();
		
		// Check each formula defined in config
		for (Formula f : config.getFormulae().values()) {
			try {
				results.add(checker.validate(f, targetPath, enumerateRandom, pack, targetArgs));
			} catch (Exception e) {
				logger.severe("Error validating formula " + f + "\n");
			}
		}
		
		// Relay results back to user
		for (Result r : results) {
			System.out.println(r.getMessage());
		}
	}
	
	
	public static Options buildOptions(String[] args) {
		Options options = new Options();
		
		// Required Options
		options.addRequiredOption("c", "configPath", true, "config gile path");
		options.addRequiredOption("t", "targetPath", true, "target file path");
		
		// Optional Options 
		options.addOption(new Option("e", "enumerateRandom", false, "consider randomness"));
		options.addOption(new Option("p", "package", false, "target contains package declaration"));
		options.addOption(new Option("args", "targetArgs", true, "cmd line arguments for target"));
		
		return options;
	}
	
}

