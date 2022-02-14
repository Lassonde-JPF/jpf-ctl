package view;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import logging.Logger;
import model.CTL;
import model.ModelChecker;
import model.Target;
import model.TransitionSystem;

import org.apache.commons.cli.*;

import controller.CMD.CTLController;
import controller.CMD.JPFController;
import controller.CMD.Manager;
import controller.CMD.TargetController;
import controller.CMD.TransitionSystemController;
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
		boolean parallel = false;
		try {
			CommandLine cmd = new DefaultParser().parse(options, args);
			// Build Options
			targetPath = cmd.getOptionValue("targetPath");
			ctlPath = cmd.getOptionValue("ctlPath");
			parallel = cmd.hasOption("parallel");
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
			target = TargetController.parseTarget(targetPath);
		} catch (ParseException e) {
			logger.severe("Error building target object for Target: path=" + targetPath + " : Error=" + e.getMessage());
			System.exit(1);
		}
		logger.info("Done.");

		// Build CTL object
		logger.info("Building CTL object...");
		CTL ctl = null;
		try {
			ctl = CTLController.parseCTL(ctlPath, target.getPath());
		} catch (IOException e) {
			logger.severe("Error parsing ctl.properties file for CTLParser: path=" + ctlPath + ", target=" + target
					+ " : Error=" + e);
			System.exit(1);
		}
		logger.info("Done.");

		// Run JPF on Target w/ appropriate labels
		logger.info("Running JPF on Target...");
		JPFController jpfController = new JPFController(target, ctl.getLabels());
		try {
			jpfController.runJPF();
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
			pts = TransitionSystemController.parseTransitionSystem(target.getName(), ctl.getReverseJNIMapping(), true);
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
			manager = new Manager(pts, ctl.getFormulaMapping());
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
			results = parallel ? manager.validateParallel(60, TimeUnit.SECONDS) : manager.validateSequentially();
		} catch (InterruptedException e) {
			logger.severe("Error validating formula : Error=" + e);
			System.exit(1);
		} catch (ExecutionException e) {
			logger.severe("Error validating formula : Error=" + e);
			System.exit(1);
		} catch (TimeoutException e) {
			logger.severe("Validation timed out at " + 60 + TimeUnit.SECONDS + " : Error=" + e);
			System.exit(1);
		}
		logger.info("Done.");

		// Relay Results
		results.entrySet().stream().forEach(
				resultEntry -> logger.info("Formula: " + resultEntry.getKey() + ", " + resultEntry.getValue()));
	}

}
