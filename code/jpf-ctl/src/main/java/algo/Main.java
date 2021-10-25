package algo;

import java.util.ArrayList;
import java.util.List;

import config.Result;
import config.StructuredCTLConfig;
import ctl.Formula;
import logging.Logger;

public class Main {
	
	// main entry loop for command line version
	public static void main(String[] args) {
		
		String configPath = args[0];
		String targetPath = args[1];
		
		// Initialize logging service/obj
		Logger logger = new Logger(Main.class.getName());
		logger.setOutputFile("jpf-ctl-" + System.currentTimeMillis());
		logger.fine("Model Checking Started");
		
		// Load config
		StructuredCTLConfig config = null;
		try {
			config = new StructuredCTLConfig(configPath, logger);
		} catch (Exception e) {
			logger.severe("Error loading config file: " + configPath);
		}
		
		// Build checker and results object(s)
		Checker checker = new Checker(config, logger);
		List<Result> results = new ArrayList<Result>();
		
		// Check each formula defined in config
		for (Formula f : config.getFormulae()) {
			try {
				results.add(checker.validate(f, targetPath, "true", true, ""));
			} catch (Exception e) {
				logger.severe("Error validating formula " + f);
			}
		}
		
		// Relay results back to user
		for (Result r : results) {
			System.out.println(r.getMessage());
		}
	}
	
}

