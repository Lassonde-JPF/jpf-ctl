package algo;

import java.io.IOException;
import java.util.Set;

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
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import listeners.PartialTransitionSystemListener;

public class ModelChecker {

	Formula formula;

	LabelledPartialTransitionSystem pts;
	
	Set<String> labels;

	static final String[] args = new String[] {
			"+cg.enumerate_random=true",
			"+listener+=,listeners.PartialTransitionSystemListener", 
			"+listener+=,label.StateLabelText" // TODO need to define the classpath (native) in jpf.properties
	};

	/*
	 * In order.. 1. ParseFormula 2. Get APs of interest 3. Build Properties for JPF
	 * (two listeners, etc.) 4. Run JPF on TargetSystem w/ APs for jpf-label 5.
	 * Identify output files 6. build a LabelledPartialTransitionSystem with the
	 * output files 7. Model Check the formula against the pts 8. return the result
	 * (as a boolean for now)
	 */
	public ModelChecker(String Formula, String TargetSystem) {
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
		
		labels = fE.getAPs();

		this.formula = new Generator().visit(tree);
	}

	public boolean check() throws IOException {
		try {
			// this initializes the JPF configuration from default.properties,
			// site.properties
			// configured extensions (jpf.properties), current directory (jpf.properies) and
			// command line args ("+<key>=<value>" options and *.jpf)
			Config conf = JPF.createConfig(args);

			// ... modify config according to your needs
			conf.setProperty("my.property", "whatever");

			JPF jpf = new JPF(conf);

			// ... explicitly create listeners (could be reused over multiple JPF runs)
			PartialTransitionSystemListener ptsListener = new PartialTransitionSystemListener(jpf.getConfig(), jpf);

			// ... set your listeners
			jpf.addListener(ptsListener);

			jpf.run();
			if (jpf.foundErrors()) {
				// ... process property violations discovered by JPF
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
		
		String jpfLabelFile = "";
		String listenerFile = "";
		
		pts = new LabelledPartialTransitionSystem(jpfLabelFile, listenerFile);
		
		Model m = new Model(pts);
		
		StateSets result = m.check(formula);
		
		return result.getSat().contains(0); //is the initial state satisfied ?
	}

}