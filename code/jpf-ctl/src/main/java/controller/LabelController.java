package controller;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.label.LabelLexer;

import labels.Label;

/**
 * JPF Controller
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class LabelController {

	/**
	 * Parses a given string representation of a label potentially using a
	 * particular classpath
	 * 
	 * @param path  - classpath of target
	 * @param input - string representation of a label object
	 * @return Label - a Label object
	 */
	public static Label parseLabel(String path, String input) {
		CharStream inputStream = CharStreams.fromString(input);
		ParseTree pT = new org.label.LabelParser(new CommonTokenStream(new LabelLexer(inputStream))).label();
		return new labels.Generator(path).visit(pT);
	}
}
