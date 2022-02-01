package controllers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.label.LabelLexer;

import labels.Label;

public class LabelParser {

	public static Label parseLabel(String path, String input) {
		CharStream inputStream = CharStreams.fromString(input);
		ParseTree pT = new org.label.LabelParser(new CommonTokenStream(new LabelLexer(inputStream))).label();
		return new labels.Generator(path).visit(pT);
	}
}
