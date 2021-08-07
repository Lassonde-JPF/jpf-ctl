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
import error.CTLError;
import error.FieldExists;
import error.ModelCheckingException;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;

public class ModelChecker {

	/*
	 * In order.. 1. ParseFormula 2. Get APs of interest 3. Build Properties for JPF
	 * (two listeners, etc.) 4. Run JPF on TargetSystem w/ APs for jpf-label 5.
	 * Identify output files 6. build a LabelledPartialTransitionSystem with the
	 * output files 7. Model Check the formula against the pts 8. return the result
	 * (as a boolean for now)
	 */
	public static boolean validate(String Formula, String TargetSystem) throws ModelCheckingException {
		System.out.println("Input Formula: " + Formula);
		System.out.println("Input System: " + TargetSystem);
		
		// Build and Check Formula before examining target system
		CharStream input = CharStreams.fromString(Formula);
		CTLError error = new CTLError();
		input = error.errorCheckAndRecover(input);

		CTLLexer lexer = new CTLLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CTLParser parser = new CTLParser(tokens);

		ParseTree tree = parser.formula();

		ParseTreeWalker walker = new ParseTreeWalker();
		FieldExists fE = new FieldExists();
		walker.walk(fE, tree);

		Formula formula = new Generator().visit(tree);
		
		System.out.println("Parsed Formula: " + formula);
		System.out.println("APs: " + fE.getAPs());
		
		try {
			// this initializes the JPF configuration from default.properties,
			// site.properties
			// configured extensions (jpf.properties), current directory (jpf.properies) and
			// command line args ("+<key>=<value>" options and *.jpf)
			Config conf = JPF.createConfig(new String[]{});

			// ... modify config according to your needs
			conf.setTarget(TargetSystem);
			
			// only needed if randomization is used
			conf.setProperty("cg.enumerate_random", "true");
			
			// build the label properties
			String fields = fE.getAPs().stream().collect(Collectors.joining("; "));
			conf.setProperty("label.class", "label.BooleanStaticField");
			conf.setProperty("label.BooleanStaticField.field", fields);
			
			// set the listeners
			conf.setProperty("listener", "label.StateLabelText,listeners.PartialTransitionSystemListener");
			
			JPF jpf = new JPF(conf);

			jpf.run();
			if (jpf.foundErrors()) {
				// ... process property violations discovered by JPF
				throw new ModelCheckingException("JPF discovered an error: " + jpf.getLastError());
			}
		} catch (JPFConfigException cx) {
			// ... handle configuration exception
			// ... can happen before running JPF and indicates inconsistent configuration
			// data
			//TODO report error
			return false;
		} catch (JPFException jx) {
			// ... handle exception while executing JPF, can be further differentiated into
			// ... JPFListenerException - occurred from within configured listener
			// ... JPFNativePeerException - occurred from within MJI method/native peer
			// ... all others indicate JPF internal errors
			// TODO report error
			return false;
		}
		
		// At this point we know the files exist so now we need to load them...
		String jpfLabelFile = TargetSystem + ".lab";
		String listenerFile = TargetSystem + ".tra";
		
		try {
			LabelledPartialTransitionSystem pts = new LabelledPartialTransitionSystem(jpfLabelFile, listenerFile);
			
			System.out.println("Pts: " + pts);
			
			Model m = new Model(pts);
			
			StateSets result = m.check(formula);
			
			System.out.println(result);
			
			return result.getSat().contains(0); //is the initial state satisfied ?
		} catch (IOException e) {
			throw new ModelCheckingException("There was an error building the LabelledPartialTransitionSystem object" + e.getMessage());
		}
	}

}
