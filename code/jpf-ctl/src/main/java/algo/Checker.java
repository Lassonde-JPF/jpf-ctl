package algo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import labels.BinaryLabel;
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

		for (Formula f : this.config.getFormulae().values()) {

			try {
				Config conf = JPF.createConfig(new String[] {});

				// ... modify config according to your needs
				conf.setTarget(target.getName());

				// Set classpath to target class
				conf.setProperty("classpath", target.getPath());
				
				// Set extension
				conf.setProperty("@using", "jpf-label");
				
				// Set listeners
				conf.setProperty("listener", "label.StateLabelText;listeners.PartialTransitionSystemListener");

				// Set args
				if (!config.getTargetArgs().isEmpty()) {
					conf.setProperty("target.args", config.getTargetArgs());
				}

				// only needed if randomization is used
				conf.setProperty("cg.enumerate_random", config.getEnumerateRandom());

				// build the label properties
				conf.setProperty("label.class", config.getLabelClasses());
				config.getLabels().values().stream()
					.filter(BinaryLabel.class::isInstance)
					.map(BinaryLabel.class::cast)
					.forEach(bL -> {
						String prev = conf.getProperty(bL.labelDef());
						conf.setProperty(bL.labelDef(), prev == null ? bL.labelVal() : prev + ";" + bL.labelVal());
					});
				
				JPF jpf = new JPF(conf);

				jpf.run();
				if (jpf.foundErrors()) {
					results.add(new Result(target, f, jpf.getLastError().toString(), false));
				}
			} catch (JPFConfigException cx) {
				throw new ModelCheckingException(
						"There was an error configuring JPF, please check your settings: " + cx.getMessage());

			} catch (JPFException jx) {
				jx.printStackTrace();
				throw new ModelCheckingException(
						"JPF encountered an internal error and was forced to terminate... \n" + jx.getMessage());
			}

			// Assert listener files exist
			File labFile = new File(target.getName() + LAB_EXTENSION);
			if (!labFile.exists()) {
				throw new ModelCheckingException(labFile.getName() + " does not exist!");
			}
			File traFile = new File(target.getName() + TRA_EXTENSION);
			if (!traFile.exists()) {
				throw new ModelCheckingException(traFile.getName() + " does not exist!");
			}
			
			// build pts
			LabelledPartialTransitionSystem pts;
			try {
				pts = new LabelledPartialTransitionSystem(labFile, traFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ModelCheckingException(
						"There was an error building the LabelledPartialTransitionSystem object:\n" + e);
			}

			// cleanup files
			if (!labFile.delete()) {
				logger.warning("File: " + labFile.getName() + " was not deleted");
			}
			if (!traFile.delete()) {
				logger.warning("File: " + traFile.getName() + " was not deleted");
			}

			// perform model check
			Model m = new Model(pts);

			boolean valid = m.check(f).getSat().contains(INITIAL_STATE);

			try {
				results.add(new Result(target, f, valid ? null : m.getCounterExample(f, INITIAL_STATE), valid));
			} catch (Exception e) {
				throw new ModelCheckingException(
						"Someting went wrong when building the counter example:\n" + e.getMessage());
			}

		}
		return results;
	}
}
