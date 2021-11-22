package error;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ctl.CTLBaseListener;
import org.ctl.CTLParser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LabelChecker {

	public static void checkLabelsExist(Set<String> labels, ParseTree formulaTree) throws LabelDoesNotExistException {

		ParseTreeWalker walker = new ParseTreeWalker();
		FormulaListener listener = new FormulaListener(labels);

		walker.walk(listener, formulaTree);

		Set<String> unusedLabels = listener.getUnusedLabels();
		if (!unusedLabels.isEmpty()) {
			String msg = "It appears the following atomic propositions defined in the formula " + formulaTree.getText()
					+ " do not exist:\n" + unusedLabels.stream().collect(Collectors.joining(", "))
					+ "\nPerhaps they were defined after the formula.";
			throw new LabelDoesNotExistException(msg);
		}

	}

	static class FormulaListener extends CTLBaseListener {

		protected Set<String> labels, unusedLabels;

		public FormulaListener(Set<String> labels) {
			super();
			this.labels = labels;
			unusedLabels = new HashSet<String>();
		}

		@Override
		public void enterAtomicProposition(CTLParser.AtomicPropositionContext ctx) {
			String alias = ctx.getText();
			if (!this.labels.contains(alias)) {
				unusedLabels.add(alias);
			}
		}

		public Set<String> getUnusedLabels() {
			return this.unusedLabels;
		}
	}

}
