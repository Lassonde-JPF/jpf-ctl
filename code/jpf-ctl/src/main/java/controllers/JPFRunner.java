package controllers;

import java.util.Collection;
import java.util.stream.Collectors;

import error.ModelCheckingException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import labels.BinaryLabel;
import labels.Label;
import logging.Logger;

public class JPFRunner {

	private Target target;
	private Collection<Label> labelling;
	
	private Logger logger;

	public JPFRunner(Target target, Collection<Label> labelling) {
		this.target = target;
		this.labelling = labelling;
		this.logger = new Logger(JPFRunner.class.getSimpleName());
	}

	public void runJPF() throws ModelCheckingException {
		logger.info("runJPF: target=" + target + ", labelling=" + labelling);
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

			// move reporting to .xml
			conf.setProperty("report.console.class", "gov.nasa.jpf.report.XMLPublisher");

			// Set args
			String args = target.getArgs();
			if (!args.isEmpty()) {
				conf.setProperty("target.args", args);
			}

			// only needed if randomization is used
			conf.setProperty("cg.enumerate_random", target.getEnumerateRandom());

			// build the label properties
			conf.setProperty("label.class",
					this.labelling.stream().map(Label::classDef).distinct().collect(Collectors.joining(";")));

			// TODO logic needs cleanup
			this.labelling.stream().filter(BinaryLabel.class::isInstance).map(BinaryLabel.class::cast).forEach(bL -> {
				String prev = conf.getProperty(bL.labelDef());
				conf.setProperty(bL.labelDef(), prev == null ? bL.labelVal() : prev + ";" + bL.labelVal());
			});

			JPF jpf = new JPF(conf);

			jpf.run();
			if (jpf.foundErrors()) {
				throw new ModelCheckingException(
						"JPF discovered fundamental errors with the target application..." + jpf.getLastError());
			}
		} catch (JPFConfigException cx) {
			throw new ModelCheckingException(
					"There was an error configuring JPF, please check your settings: " + cx.getMessage());
		} catch (JPFException jx) {
			jx.printStackTrace();
			throw new ModelCheckingException(
					"JPF encountered an internal error and was forced to terminate... \n" + jx.getMessage());
		}
	}

}
