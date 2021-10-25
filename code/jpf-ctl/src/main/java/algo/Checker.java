package algo;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import config.LabelledPartialTransitionSystem;
import config.Result;
import config.StructuredCTLConfig;
import ctl.Formula;
import error.FieldExists;
import error.ModelCheckingException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import logging.Logger;

public class Checker {
	
	private StructuredCTLConfig config;
	private Logger logger;
	
	private static final int INITIAL_STATE = 0;
	private static final String LAB_EXTENSION = ".lab";
	private static final String TRA_EXTENSION = ".tra";
	
	public Checker(StructuredCTLConfig config) {
		this.config = config;
		this.logger = new Logger(Checker.class.getName(), "Checker");
	}
	
	public Result validate(Formula Formula, String path, String EnumerateRandom, boolean pack, String args)
			throws ModelCheckingException {
		
		logger.info("Validation beginning with arguments:\n\tFormula: " + Formula + "\n\tPath: " + path);
		
		// Create classpath and target values from path
		String classpath;
		String target;
		int lastSlash = path.lastIndexOf("\\");
		classpath = path.substring(0, lastSlash);
		target = path.substring(lastSlash + 1, path.lastIndexOf("."));

		if (pack) {
			target = classpath.substring(classpath.lastIndexOf("\\") + 1) + "." + target;
			classpath = classpath.substring(0, classpath.lastIndexOf("\\"));
		}

		try {
			Config conf = JPF.createConfig(new String[] {});

			// ... modify config according to your needs
			conf.setTarget(target);

			// Set classpath to parent folder
			conf.setProperty("classpath", classpath);

			// Set args
			if (!args.isEmpty()) {
				conf.setProperty("target.args", args);
			}

			// only needed if randomization is used
			conf.setProperty("cg.enumerate_random", EnumerateRandom);

			// extension jpf-label
			conf.setProperty("@using", "jpf-label");

			// set the listeners
			conf.setProperty("listener", "label.StateLabelText,listeners.PartialTransitionSystemListener");
			
			// build the label properties
			String fields = FieldExists.APs.stream().collect(Collectors.joining("; "));
			conf.setProperty("label.class", "label.BooleanStaticField");
			conf.setProperty("label.BooleanStaticField.field", fields);

			// This instantiates JPF but also adds the jpf.properties and other arguments to
			// the config
			JPF jpf = new JPF(conf);
			
			jpf.run();
			if (jpf.foundErrors()) {
				String msg = "Model Checking Finished\n For the selected class:\t" + path + "\n And the written formula:\t"
						+ Formula
						+ "\nIt has been determined that the target system contains an error that needs to be resolved before model checking can commence"
						+ "\nThe error can be seen below:\n" + jpf.getLastError();
				return new Result(msg, false);
			}
		} catch (JPFConfigException cx) {
			throw new ModelCheckingException(
					"There was an error configuring JPF, please check your settings: " + cx.getMessage());
		} catch (JPFException jx) {
			throw new ModelCheckingException(
					"JPF encountered an internal error and was forced to terminate." + jx.getMessage());
		}

		// At this point we know the files exist so now we need to load them...
		String jpfLabelFile = target + LAB_EXTENSION;
		String listenerFile = target + TRA_EXTENSION;

		// build pts
		LabelledPartialTransitionSystem pts;
		try {
			pts = new LabelledPartialTransitionSystem(jpfLabelFile, listenerFile);
		} catch (IOException e) {
			throw new ModelCheckingException(
					"There was an error building the LabelledPartialTransitionSystem object:\n" + e.getMessage());
		}

		// cleanup files
		File labFile = new File(jpfLabelFile);
		if (!labFile.delete()) {
			logger.severe("File: " + labFile.getName() + " was not deleted");
		}
		File traFile = new File(listenerFile);
		if (!traFile.delete()) {
			logger.severe("File: " + traFile.getName() + " was not deleted");
		}

		// perform model check
		Model m = new Model(pts);

		boolean valid = m.check(Formula).getSat().contains(INITIAL_STATE);
		
		// success
		if (valid) {
			String msg = "Model Checking Finished\n For the selected class:\t" + target + "\n And the written formula:\t"
					+ Formula
					+ "\nIt has been determined that the formula holds in the initial state and is considered valid for this system.";
			return new Result(msg, valid);
		}

		// fail
		try {
			String msg = "Model Checking Finished\n For the selected class:\t" + path + "\n And the written formula:\t"
					+ Formula
					+ "\nIt has been determined that the formula does not hold in the initial state and is considered invalid for this system."
					+ "\nA counter example can be seen below:\n" + m.getCounterExample(Formula, INITIAL_STATE);
			return new Result(msg, valid);
		} catch (Exception e) {
			throw new ModelCheckingException(
					"Someting went wrong when building the counter example:\n" + e.getMessage());
		}
	}
}
