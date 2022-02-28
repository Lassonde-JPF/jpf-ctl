package error;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ctl.CTLBaseListener;
import org.ctl.CTLParser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Label Checker - for ensuring correctness of <Label> objects
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class LabelChecker {

	/**
	 * Checks that all labels in a given ParseTree object have been defined (are a
	 * subset of <labels>)
	 * 
	 * @param labels      - Set<String> of known labels to compare against
	 * @param formulaTree - ParseTree object representing formula tree
	 * @throws LabelDoesNotExistException
	 */
	public static void checkLabelsExist(Set<String> labels, ParseTree formulaTree) throws LabelDoesNotExistException {
		// Define Walker and Listener objects
		ParseTreeWalker walker = new ParseTreeWalker();
		FormulaListener listener = new FormulaListener(labels);

		// Walk through the tree and collect labels
		walker.walk(listener, formulaTree);

		// See (if any) which labels are unused/undefined
		Set<String> unusedLabels = listener.getUnusedLabels();
		if (!unusedLabels.isEmpty()) {
			throw new LabelDoesNotExistException(
					"It appears the following atomic propositions defined in the formula " + formulaTree.getText()
							+ " do not exist:\n" + unusedLabels.stream().collect(Collectors.joining(", ")));
		}

	}

	/**
	 * Listener responsible for collecting the set of labels used in this formula
	 * 
	 * @author mattw
	 */
	static class FormulaListener extends CTLBaseListener {
		// Attributes
		private Set<String> labels, unusedLabels;

		/**
		 * Initializes this FormulaListener with a set of labels to compare against
		 * 
		 * @param labels - set of labels to compare against
		 */
		public FormulaListener(Set<String> labels) {
			super();
			this.labels = labels;
			unusedLabels = new HashSet<String>();
		}

		/**
		 * When entering an Atomic Proposition, check if label is contained by <labels>
		 * and if not, add to unused set
		 */
		@Override
		public void enterAtomicProposition(CTLParser.AtomicPropositionContext ctx) {
			String alias = ctx.getText();
			if (!this.labels.contains(alias)) {
				unusedLabels.add(alias);
			}
		}

		/**
		 * Returns the set of unused labels in this formula
		 * 
		 * @return - set of unused labels
		 */
		public Set<String> getUnusedLabels() {
			return this.unusedLabels;
		}
	}

}
