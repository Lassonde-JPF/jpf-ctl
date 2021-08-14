package algo;

/**
 * ModelChecker
 * 
 * @author Matt Walker
 *
 */

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;

import ctl.Formula;
import ctl.Generator;
import error.AtomicPropositionDoesNotExistException;
import error.CTLError;
import error.FieldExists;
import error.ModelCheckingException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;

public class ModelChecker {

	private static final int INITIAL_STATE = 0;
	private static final String LAB_EXTENSION = ".lab";
	private static final String TRA_EXTENSION = ".tra";

	public static String validate(String Formula, String path, String EnumerateRandom, boolean pack, String args)
			throws ModelCheckingException {

		// Create classpath and target values from path
		String classpath;
		String target;
		int lastSlash = path.lastIndexOf("\\");
		classpath = path.substring(0, lastSlash);
		target = path.substring(lastSlash + 1, path.lastIndexOf("."));

		/*
		 * If there is a package then we need to modify the classpath and target
		 * Specifically, we move the classpath up one directory and add the directory we
		 * moved up to the target as a prefix delimeted by a '.'
		 */
		if (pack) {
			target = classpath.substring(classpath.lastIndexOf("\\") + 1) + "." + target;
			classpath = classpath.substring(0, classpath.lastIndexOf("\\"));
		}
		System.out.println("classpath: " + classpath);
		System.out.println("target: " + target);

		// Build and Check Formula before examining target system
		CharStream input = CharStreams.fromString(Formula);
		input = new CTLError().errorCheckAndRecover(input);

		CTLParser parser = new CTLParser(new CommonTokenStream(new CTLLexer(input)));
		ParseTree tree = parser.formula();

		/*
		 * Perform Error Checking on input formula and gather APs for use with jpf-ctl
		 */
		ParseTreeWalker walker = new ParseTreeWalker();
		try {
			walker.walk(new FieldExists(classpath), tree); // TODO fix
		} catch (AtomicPropositionDoesNotExistException e) {
			// throw new ModelCheckingException(e.getMessage());
		}

		// At this point we know the formula is correct.
		Formula formula = new Generator().visit(tree);

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

			System.out.println("APs: " + fields);

			// This instantiates JPF but also adds the jpf.properties and other arguments to
			// the config
			JPF jpf = new JPF(conf);

			System.out.println("JPF Classpath: " + jpf.getConfig().getProperty("classpath"));

			jpf.run();
			if (jpf.foundErrors()) {
				return "Model Checking Finished\n For the selected class:\t" + path + "\n And the written formula:\t"
						+ formula
						+ "\nIt has been determined that the target system contains an error that needs to be resolved before model checking can commence"
						+ "\nThe error can be seen below:\n" + jpf.getLastError();
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
			System.err.println("File: " + labFile.getName() + " was not deleted");
		}
		File traFile = new File(listenerFile);
		if (!traFile.delete()) {
			System.err.println("File: " + traFile.getName() + " was not deleted");
		}

		// perform model check
		Model m = new Model(pts);

		// success
		if (m.check(formula).getSat().contains(INITIAL_STATE)) {
			return "Model Checking Finished\n For the selected class:\t" + target + "\n And the written formula:\t"
					+ Formula
					+ "\nIt has been determined that the formula holds in the initial state and is considered valid for this system.";
		}

		// fail
		try {
			return "Model Checking Finished\n For the selected class:\t" + path + "\n And the written formula:\t"
					+ formula
					+ "\nIt has been determined that the formula does not hold in the initial state and is considered invalid for this system."
					+ "\nA counter example can be seen below:\n" + m.getCounterExample(formula, INITIAL_STATE);
		} catch (Exception e) {
			throw new ModelCheckingException(
					"Someting went wrong when building the counter example:\n" + e.getMessage());
		}
	}

}
