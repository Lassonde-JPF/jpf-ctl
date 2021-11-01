package algo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import config.LabelledPartialTransitionSystem;
import config.Result;
import config.StructuredCTLConfig;
import config.Target;
import ctl.Formula;
import error.ModelCheckingException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import label.StateLabelText;
import label.Type;
import listeners.PartialTransitionSystemListener;
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

	public List<Result> validate() throws ModelCheckingException {

		List<Result> results = new ArrayList<Result>();

		Target target = this.config.getTarget();

		for (Formula f : this.config.getFormulae()) {

			logger.info("Validation beginning with arguments:\n\tFormula: " + f + "\n\tPath: " + target.getPath());

			try {
				Config conf = JPF.createConfig(new String[] {});

				// ... modify config according to your needs
				conf.setTarget(target.getName());
				System.out.println(target);

				// Set classpath to target class
				conf.setProperty("classpath", target.getPath());

				// Set args
				if (!config.getTargetArgs().isEmpty()) {
					conf.setProperty("target.args", config.getTargetArgs());
				}

				// only needed if randomization is used
				conf.setProperty("cg.enumerate_random", config.getEnumerateRandom());

				conf.setProperty("@using", "jpf-ctl"); // SHouldn't be requires as it's defined in jpf.properties

				// Set Listeners
				conf.setProperty("listener", "label.StateLabelText;listeners.PartialTransitionSystemListener");

				// build the label properties
				conf.setProperty("label.class", config.getLabelClasses());
				for (Type t : config.getUniqueTypes()) {
					conf.setProperty(Type.labelDef(t), config.getLabelsOfType(t)); // TODO causing jpf exception (second
																					// arg)
				}

				// This instantiates JPF but also adds the jpf.properties and other arguments to
				// the config
				JPF jpf = new JPF(conf);

				// Print for now the labels and stuff just debugging ya feel?
				System.out.println("label.class = " + jpf.getConfig().getProperty("label.class"));
				for (Type t : config.getUniqueTypes()) {
					System.out.println(Type.labelDef(t) + " = " + jpf.getConfig().getProperty(Type.labelDef(t)));
				}

				jpf.run();
				if (jpf.foundErrors()) {
					String msg = "Model Checking Finished\n For the selected class:\t" + target.getName()
							+ "\n And the written formula:\t" + f
							+ "\nIt has been determined that the target system contains an error that needs to be resolved before model checking can commence"
							+ "\nThe error can be seen below:\n" + jpf.getLastError();
					results.add(new Result(msg, false));
				}
			} catch (JPFConfigException cx) {
				throw new ModelCheckingException(
						"There was an error configuring JPF, please check your settings: " + cx.getMessage());

			} catch (JPFException jx) {
				jx.printStackTrace();
				throw new ModelCheckingException(
						"JPF encountered an internal error and was forced to terminate... \n" + jx.getMessage());
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

			boolean valid = m.check(f).getSat().contains(INITIAL_STATE);

			// success
			if (valid) {
				String msg = "Model Checking Finished\n For the selected class:\t" + target.getName()
						+ "\n And the written formula:\t" + f
						+ "\nIt has been determined that the formula holds in the initial state and is considered valid for this system.";
				results.add(new Result(msg, valid));
			}

			// fail
			try {
				String msg = "Model Checking Finished\n For the selected class:\t" + target.getName()
						+ "\n And the written formula:\t" + f
						+ "\nIt has been determined that the formula does not hold in the initial state and is considered invalid for this system."
						+ "\nA counter example can be seen below:\n" + m.getCounterExample(f, INITIAL_STATE);
				results.add(new Result(msg, valid));
			} catch (Exception e) {
				throw new ModelCheckingException(
						"Someting went wrong when building the counter example:\n" + e.getMessage());
			}

		}
		return results;
	}
}
