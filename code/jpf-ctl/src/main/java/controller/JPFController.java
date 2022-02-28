package controller;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import error.ModelCheckingException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import labels.BinaryLabel;
import labels.Label;
import logging.Logger;
import model.Target;

/**
 * JPF Controller
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class JPFController {
	
	// Attributes (class specific)
	private Target target;
	private Collection<Label> labelling;
	
	// Logging
	private Logger logger;

	/**
	 * Initializes this JPF Controller for a given Target and collection of labels 
	 * 
	 * @param target - Target to configure for jpf-core
	 * @param labelling - Collection of labels for jpf-label
	 */
	public JPFController(Target target, Collection<Label> labelling) {
		this.target = target;
		this.labelling = labelling;
		this.logger = new Logger(JPFController.class.getSimpleName());
	}
	
	/**
	 * Runs JPF on Target (produces two files: <target>.lab, <target>.tra)
	 * 
	 * @throws ModelCheckingException
	 */
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
			
			// Set jpfArgs
			for (Entry<String, String> e : target.getJpfArgs().entrySet()) {
				conf.setProperty(e.getKey(), e.getValue());
			}

			// build the label class'
			conf.setProperty("label.class",
					this.labelling.stream().map(Label::classDef).distinct().collect(Collectors.joining(";")));

			// Apply label values
			this.labelling.stream().filter(BinaryLabel.class::isInstance).map(BinaryLabel.class::cast).forEach(bL -> {
				String prev = conf.getProperty(bL.labelDef());
				conf.setProperty(bL.labelDef(), prev == null ? bL.labelVal() : prev + ";" + bL.labelVal());
			});

			// Build JPF object
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
