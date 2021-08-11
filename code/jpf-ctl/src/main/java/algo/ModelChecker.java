package algo;

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

	/*
	 * 1. Get name of system to test "example.Example"
	 * 2. Get absolute path of system to test
	 * 3. Use parent directory of absolute path as classpath
	 * 4. Append the classpath as a property (argument might be better?)
	 */
	public static boolean validate(String Formula, String TargetSystem, String EnumerateRandom) throws ModelCheckingException {
		
		// Build and Check Formula before examining target system
		CharStream input = CharStreams.fromString(Formula);
		input = new CTLError().errorCheckAndRecover(input);

		CTLParser parser = new CTLParser(new CommonTokenStream(new CTLLexer(input)));
		ParseTree tree = parser.formula();

		ParseTreeWalker walker = new ParseTreeWalker();
		try {
			walker.walk(new FieldExists(), tree);
		} catch (AtomicPropositionDoesNotExistException e) {
			throw new ModelCheckingException(e.getMessage());
		}

		// At this point we know the formula is correct. 
		Formula formula = new Generator().visit(tree);
	
		int lastSlash = TargetSystem.lastIndexOf("/") + 1; 
		if (lastSlash == 0) {
			lastSlash = TargetSystem.lastIndexOf("\\") + 1;
		}
		// break apart absolute path
		String classpath = TargetSystem.substring(0, lastSlash-1);
		String target = TargetSystem.substring(lastSlash, TargetSystem.lastIndexOf("."));
		
		System.out.println("Classpath: " + classpath);
		System.out.println("target: " + target);
		
		try {
			Config conf = JPF.createConfig(new String[]{"+classpath+=" + classpath});
			
			// ... modify config according to your needs
			conf.setTarget(target);
			
			// only needed if randomization is used
			conf.setProperty("cg.enumerate_random", EnumerateRandom);
			
			// build the label properties
			String fields = FieldExists.APs.stream().collect(Collectors.joining("; "));
			conf.setProperty("label.class", "label.BooleanStaticField");
			conf.setProperty("label.BooleanStaticField.field", fields);
			
			// set the listeners
			conf.setProperty("listener", "label.StateLabelText,listeners.PartialTransitionSystemListener");
			
			JPF jpf = new JPF(conf);
			
			System.out.println("JPF Classpath: " + jpf.getConfig().getProperty("classpath"));

			jpf.run();
			if (jpf.foundErrors()) {
				// If an error is found here then it is deadlock / racecondition etc. 
			}
		} catch (JPFConfigException cx) {
			throw new ModelCheckingException("There was an error configuring JPF, please check your settings.");
		} catch (JPFException jx) {
			throw new ModelCheckingException("JPF encountered an internal error and was forced to terminate.");
		}
		
		// At this point we know the files exist so now we need to load them...
		String jpfLabelFile = TargetSystem + ".lab";
		String listenerFile = TargetSystem + ".tra";
		
		try {
			LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem(jpfLabelFile, listenerFile);
			
			StateSets result = new Model(pts).check(formula);
			
			return result.getSat().contains(0); //is the initial state satisfied ?
		} catch (IOException e) {
			throw new ModelCheckingException("There was an error building the LabelledPartialTransitionSystem object" + e.getMessage());
		}
	}

}
