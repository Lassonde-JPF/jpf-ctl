package labels;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.label.LabelLexer;
import org.label.LabelParser;

public class BaseTest {
	protected Generator generator;
	
	/**
	 * Number of random cases that are considered in each test.
	 */
	protected static final int CASES = 1000;
	
	protected static final String PATH = "src/test/java";

	/**
	 * Initializes this test.
	 */
	public BaseTest() {
		this.generator = new Generator(PATH);
	}

	/**
	 * Translates a syntactically correct label from its string representation to a
	 * parse tree.
	 * 
	 * @param label
	 * @return parse tree corresponding to the given label
	 */
	public static ParseTree parse(String label) {
		CharStream input = CharStreams.fromString(label);
		LabelLexer lexer = new LabelLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LabelParser parser = new LabelParser(tokens);
		ParseTree tree = parser.label();
		return tree;
	}
}
