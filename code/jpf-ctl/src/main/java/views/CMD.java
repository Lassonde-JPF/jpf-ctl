package views;

import java.io.IOException;
import java.util.Map;

import logging.Logger;
import model.ModelChecker;

import org.apache.commons.cli.*;

import controllers.CTLParser;
import controllers.JPFRunner;
import controllers.Manager;
import controllers.Target;
import controllers.TransitionSystem;
import error.ModelCheckingException;

public class CMD {

	// Static Variables
	private static final String JPF_CTL = "jpf-ctl";

	// Help Printer
	private static final String HEADER = "\n" + "    o8o             .o88o.                       .   oooo  \r\n"
			+ "    `\"'             888 `\"                     .o8   `888  \r\n"
			+ "   oooo oo.ooooo.  o888oo           .ooooo.  .o888oo  888  \r\n"
			+ "   `888  888' `88b  888            d88' `\"Y8   888    888  \r\n"
			+ "    888  888   888  888    8888888 888         888    888  \r\n"
			+ "    888  888   888  888            888   .o8   888 .  888  \r\n"
			+ "    888  888bod8P' o888o           `Y8bod8P'   \"888\" o888o \r\n"
			+ "    888  888                                               \r\n"
			+ ".o. 88P o888o                                              \r\n"
			+ "`Y888P                                                     \n\n";
	private static final String FOOTER = "\nPlease report issues at https://github.com/Lassonde-JPF/jpf-ctl/issues";

	// main entry loop for command line version test
	public static void main(String[] args) {

		// Parse command line arguments
		Options options = new Options();

		// Required Options
		options.addRequiredOption("c", "ctlPath", true, "path to ctl properties file");
		options.addOption("t", "targetPath", true, "path to target properties file");

		// Optional Options
		options.addOption(new Option("v", "verboseLogs", false, "enable logging to print to console"));
		options.addOption(new Option("p", "parallel", false, "enable parallel validation of formulae"));

		// Parse Options
		String targetPath = null, ctlPath = null;
		try {
			CommandLine cmd = new DefaultParser().parse(options, args);
			// Build Options
			targetPath = cmd.getOptionValue("targetPath");
			ctlPath = cmd.getOptionValue("ctlPath");
			Logger.setEnabled(cmd.hasOption("v"));
		} catch (ParseException e) {
			new HelpFormatter().printHelp(JPF_CTL, HEADER, options, FOOTER, true);
			System.out.println("\n" + e.getMessage());
			System.exit(1);
		}

		// Initialize logging service/obj
		Logger logger = new Logger(CMD.class.getSimpleName());
		logger.info("Initializing logging service...");
		try {
			logger.setOutputFile();
		} catch (SecurityException | IOException e) {
			logger.warning(
					"Error adding file handler to logger, logs will not be saved for this execution : Error=" + e);
		}
		logger.info("Done.");

		// Build Target
		logger.info("Building target object...");
		Target target = null;
		try {
			target = new Target(targetPath);
		} catch (ParseException e) {
			logger.severe("Error building target object for Target: path=" + targetPath + " : Error=" + e.getMessage());
			System.exit(1);
		}
		logger.info("Done.");

		// Build CTL object
		logger.info("Building CTL object...");
		CTLParser ctl = null;
		try {
			ctl = new CTLParser(ctlPath, target);
		} catch (IOException e) {
			logger.severe("Error parsing ctl.properties file for CTLParser: path=" + ctlPath + ", target=" + target
					+ " : Error=" + e);
			System.exit(1);
		}
		logger.info("Done.");

		// Run JPF on Target w/ appropriate labels
		logger.info("Running JPF on Target...");
		JPFRunner jpfRunner = new JPFRunner(target, ctl.getLabels());
		try {
			jpfRunner.runJPF();
		} catch (ModelCheckingException e) {
			logger.severe("Error running JPF on Target for JPFRunner: target=" + target + ", labels=" + ctl.getLabels()
					+ " : Error=" + e);
			System.exit(1);
		}
		logger.info("Done.");

		// Build Transition System
		logger.info("Building Transition System...");
		TransitionSystem pts = null;
		try {
			pts = new TransitionSystem(target.getName(), true);
		} catch (IOException e) {
			logger.severe("Error building Transistion System for TransitionSystem: target=" + target.getName()
					+ " : Error=" + e);
			System.exit(1);
		}
		logger.info("Done.");

		// Build Manager Object
		logger.info("Building Manager...");
		Manager manager = null;
		try {
			manager = new Manager(pts, ctl.getJNIMapping(), ctl.getFormulaMapping());
		} catch (Exception e) {
			logger.severe("Error initializing manager for ModelCheckingManager: pts=" + pts + ", jniMapping="
					+ ctl.getJNIMapping() + ", formulas=" + ctl.getFormulaMapping() + " : Error=" + e);
			System.exit(1);
		}
		logger.info("Done.");

		// Begin Model Checking
		logger.info("Beginning Model Checking...");
		Map<String, ModelChecker.Result> results = null;
		try {
			results = manager.validateSequentially();
		} catch (Exception e) {
			logger.severe("Error validating: " + e.getMessage());
			System.exit(1);
		}
		logger.info("Done.");

		// Relay Results
		results.entrySet().stream().forEach(
				resultEntry -> logger.info("Formula: " + resultEntry.getKey() + ", " + resultEntry.getValue()));
	}

}
